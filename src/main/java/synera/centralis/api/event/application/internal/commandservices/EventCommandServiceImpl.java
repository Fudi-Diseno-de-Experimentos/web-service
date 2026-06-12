
package synera.centralis.api.event.application.internal.commandservices;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import synera.centralis.api.iam.infrastructure.authorization.sfs.utils.SecurityUtils;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.commands.DeleteEventCommand;
import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.event.infrastructure.persistence.jpa.repositories.EventRepository;
import synera.centralis.api.shared.domain.events.EventCreatedEvent;
import synera.centralis.api.shared.domain.exceptions.DuplicateResourceException;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;
import synera.centralis.api.event.domain.model.valueobjects.SpaceId;

/**
 * Implementation of EventCommandService.
 * Handles all event-related command operations with business logic.
 */
@Slf4j
@Service
public class EventCommandServiceImpl implements EventCommandService {

    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EventCommandServiceImpl(EventRepository eventRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Event handle(CreateEventCommand command) {
        // Day-level room booking guard, outside the try so the 409 isn't masked as a 400.
        if (command.spaceId() != null) {
            verifySpaceAvailable(command.companyId(), command.spaceId(), command.date(), null);
        }
        try {
            log.info("Creating new event with title: {}", command.title());

            var event = new Event(
                    command.title(),
                    command.description(),
                    command.date(),
                    command.location(),
                    command.recipientIds(),
                    command.createdBy()
            );

            event.setCompanyId(command.companyId());
            if (command.spaceId() != null) {
                event.setSpaceId(new SpaceId(command.spaceId()));
            }

            var savedEvent = eventRepository.save(event);

            log.info("=== EVENT CREATED ===");
            log.info("Event Title: {}", savedEvent.getTitle());
            log.info("Event ID: {}", savedEvent.getId());
            log.info("Event Date: {}", savedEvent.getDate());
            log.info("Created By: {}", savedEvent.getCreatedBy().userId());

            // Safely extract recipient IDs (evita NPE si recipients es null)
            Set<UUID> recipientIds = savedEvent.getRecipients() == null
                    ? Collections.emptySet()
                    : savedEvent.getRecipients().stream()
                            .map(recipient -> recipient.userId())
                            .collect(Collectors.toSet());

            log.info("Recipients count: {}", recipientIds.size());
            log.info("Recipient IDs: {}", recipientIds);

            // Publish event created domain event for notifications
            log.info("PUBLISHING EVENT CREATED EVENT for event: {}", savedEvent.getId());

            var domainEvent = EventCreatedEvent.create(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getDate(),
                savedEvent.getCreatedBy().userId(),
                recipientIds
            );

            log.info("Domain event created: {}", domainEvent.toString());
            eventPublisher.publishEvent(domainEvent);
            log.info("Event creation domain event published successfully");

            log.info("Successfully created event with ID: {}", savedEvent.getId());

            return savedEvent;

        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage(), e);
            throw new ValidationException("Error creating event: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Event handle(UpdateEventCommand command) {
        log.info("Updating event with ID: {}", command.eventId());

        var event = eventRepository.findByIdAndCompanyId(command.eventId(), command.companyId())
                .orElseThrow(() -> {
                    log.warn("Event not found with ID: {}", command.eventId());
                    return new ResourceNotFoundException("Event not found with ID: " + command.eventId());
                });

        // Resolve the post-update space/date (a null field on the command means "leave as-is")
        // and re-check the day-level conflict, excluding this event from its own check.
        UUID effectiveSpaceId = command.spaceId() != null
                ? command.spaceId()
                : (event.getSpaceId() != null ? event.getSpaceId().spaceId() : null);
        LocalDateTime effectiveDate = command.date() != null ? command.date() : event.getDate();
        if (effectiveSpaceId != null) {
            verifySpaceAvailable(command.companyId(), effectiveSpaceId, effectiveDate, command.eventId());
        }

        try {
            // Update event information
            event.updateEvent(
                    command.title(),
                    command.description(),
                    command.date(),
                    command.location(),
                    command.recipientIds()
            );
            if (command.spaceId() != null) {
                event.setSpaceId(new SpaceId(command.spaceId()));
            }

            var updatedEvent = eventRepository.save(event);

            log.info("Successfully updated event with ID: {}", updatedEvent.getId());

            return updatedEvent;

        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating event: {}", e.getMessage(), e);
            throw new ValidationException("Error updating event: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean handle(DeleteEventCommand command) {
        log.info("Deleting event with ID: {}", command.eventId());

        if (!eventRepository.existsByIdAndCompanyId(command.eventId(), command.companyId())) {
            log.warn("Event not found with ID: {}", command.eventId());
            throw new ResourceNotFoundException("Event not found with ID: " + command.eventId());
        }

        try {
            eventRepository.deleteById(command.eventId());
            log.info("Successfully deleted event with ID: {}", command.eventId());
            return true;
        } catch (Exception e) {
            log.error("Error deleting event: {}", e.getMessage(), e);
            throw new ValidationException("Error deleting event: " + e.getMessage());
        }
    }

    /**
     * Throws {@link DuplicateResourceException} (→ 409) if the space is already booked
     * by another event in the company on the same calendar day as {@code date}.
     * Time-of-day is ignored: a room can hold at most one event per day.
     *
     * @param excludeEventId the event being updated, excluded from its own check; null on create
     */
    private void verifySpaceAvailable(CompanyId companyId, UUID spaceId, LocalDateTime date, UUID excludeEventId) {
        LocalDateTime dayStart = date.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        boolean conflict = eventRepository.existsBookingConflict(
                companyId, new SpaceId(spaceId), dayStart, dayEnd, excludeEventId);
        if (conflict) {
            throw new DuplicateResourceException(
                    "Space is already booked on " + date.toLocalDate() + " for this company");
        }
    }
}

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

import lombok.extern.slf4j.Slf4j;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.commands.DeleteEventCommand;
import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.event.infrastructure.persistence.jpa.repositories.EventRepository;
import synera.centralis.api.shared.domain.events.EventCreatedEvent;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;

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
            log.info("👥 Recipient IDs: {}", recipientIds);

            // Publish event created domain event for notifications
            log.info("🚀 PUBLISHING EVENT CREATED EVENT for event: {}", savedEvent.getId());

            var domainEvent = EventCreatedEvent.create(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getDate(),
                savedEvent.getCreatedBy().userId(),
                recipientIds
            );

            log.info("📋 Domain event created: {}", domainEvent.toString());
            eventPublisher.publishEvent(domainEvent);
            log.info("✅ Event creation domain event published successfully");

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

        try {
            // Update event information
            event.updateEvent(
                    command.title(),
                    command.description(),
                    command.date(),
                    command.location(),
                    command.recipientIds()
            );

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
}
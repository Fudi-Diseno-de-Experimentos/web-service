
package synera.centralis.api.event.application.internal.commandservices;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.commands.DeleteEventCommand;
import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.event.infrastructure.persistence.jpa.repositories.EventRepository;
import synera.centralis.api.shared.domain.events.EventCreatedEvent;

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
    public Optional<Event> handle(CreateEventCommand command) {
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

            return Optional.of(savedEvent);

        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Event> handle(UpdateEventCommand command) {
        try {
            log.info("Updating event with ID: {}", command.eventId());

            var eventOpt = eventRepository.findById(command.eventId());

            if (eventOpt.isEmpty()) {
                log.warn("Event not found with ID: {}", command.eventId());
                return Optional.empty();
            }

            var event = eventOpt.get();

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

            return Optional.of(updatedEvent);

        } catch (Exception e) {
            log.error("Error updating event: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<UUID> handle(DeleteEventCommand command) {
        try {
            log.info("Deleting event with ID: {}", command.eventId());

            if (!eventRepository.existsById(command.eventId())) {
                log.warn("Event not found with ID: {}", command.eventId());
                return Optional.empty();
            }

            eventRepository.deleteById(command.eventId());

            log.info("Successfully deleted event with ID: {}", command.eventId());

            return Optional.of(command.eventId());

        } catch (Exception e) {
            log.error("Error deleting event: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
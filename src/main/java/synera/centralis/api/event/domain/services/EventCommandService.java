package synera.centralis.api.event.domain.services;

import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.commands.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain service interface for handling event-related commands.
 * Defines the contract for all event command operations.
 */
public interface EventCommandService {

    /**
     * Handles the creation of a new event.
     * @param command the create event command
     * @return the created event or empty if creation failed
     */
    Optional<Event> handle(CreateEventCommand command);

    /**
     * Handles updating event information.
     * @param command the update event command
     * @return the updated event or empty if update failed
     */
    Optional<Event> handle(UpdateEventCommand command);

    /**
     * Handles deleting an event.
     * @param command the delete event command
     * @return the deleted event ID or empty if deletion failed
     */
    Optional<UUID> handle(DeleteEventCommand command);
}

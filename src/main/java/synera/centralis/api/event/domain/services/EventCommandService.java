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
     * @return the created event
     */
    Event handle(CreateEventCommand command);

    /**
     * Handles updating event information.
     * @param command the update event command
     * @return the updated event
     */
    Event handle(UpdateEventCommand command);

    /**
     * Handles deleting an event.
     * @param command the delete event command
     */
    boolean handle(DeleteEventCommand command);

    /**
     * Handles a member accepting or declining their event invitation.
     * @param command the respond-to-invitation command
     * @return the updated event
     */
    Event handle(RespondToEventInvitationCommand command);
}

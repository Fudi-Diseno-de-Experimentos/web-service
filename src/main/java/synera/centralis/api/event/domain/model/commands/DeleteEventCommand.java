package synera.centralis.api.event.domain.model.commands;

import java.util.UUID;

/**
 * Command to delete an event.
 */
public record DeleteEventCommand(UUID eventId) {
    public DeleteEventCommand {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
}



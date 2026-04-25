package synera.centralis.api.event.domain.model.commands;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Command to update an existing event.
 */
public record UpdateEventCommand(
        UUID eventId,
        String title,
        String description,
        LocalDateTime date,
        String location,
        List<UUID> recipientIds
) {
    public UpdateEventCommand {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
}


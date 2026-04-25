package synera.centralis.api.event.domain.model.commands;

import synera.centralis.api.event.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new event.
 * Contains all necessary information to create an event.
 */
public record CreateEventCommand(
        String title,
        String description,
        LocalDateTime date,
        String location,
        List<UUID> recipientIds,
        UserId createdBy
) {
    public CreateEventCommand {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be null or empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        if (recipientIds == null || recipientIds.isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one recipient");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }
    }
}
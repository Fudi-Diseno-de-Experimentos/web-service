package synera.centralis.api.dashboard.domain.model.commands;

import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;
import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;

/**
 * Command to register an event view
 * Represents the intention to track that a user has viewed an event
 */
public record RegisterEventViewCommand(
        UserId userId,
        EventId eventId
) {
    
    public RegisterEventViewCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
}
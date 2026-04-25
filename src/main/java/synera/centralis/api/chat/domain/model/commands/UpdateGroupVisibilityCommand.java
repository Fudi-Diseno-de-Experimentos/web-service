package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;

import java.util.UUID;

/**
 * Command to update group visibility (PUBLIC/PRIVATE).
 * Separated from general group updates due to different security implications.
 */
public record UpdateGroupVisibilityCommand(
        UUID groupId,
        GroupVisibility visibility
) {
    public UpdateGroupVisibilityCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (visibility == null) {
            throw new IllegalArgumentException("Group visibility cannot be null");
        }
    }
}
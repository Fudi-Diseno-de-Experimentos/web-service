package synera.centralis.api.chat.domain.model.commands;

import java.util.UUID;

/**
 * Command to delete a group from the chat system.
 * This will also delete all associated messages.
 */
public record DeleteGroupCommand(
        UUID groupId
) {
    public DeleteGroupCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
    }
}
package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Command to create a new message in a group.
 * Contains all necessary information to send a message.
 */
public record CreateMessageCommand(
        UUID groupId,
        UserId senderId,
        String body
) {
    public CreateMessageCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be null or empty");
        }
        if (body.length() > 1000) {
            throw new IllegalArgumentException("Message body cannot exceed 1000 characters");
        }
    }
}
package synera.centralis.api.chat.domain.model.commands;

import java.util.UUID;

/**
 * Command to update the body content of a message.
 * Only the sender can edit their own messages.
 */
public record UpdateMessageBodyCommand(
        UUID messageId,
        String newBody
) {
    public UpdateMessageBodyCommand {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
        if (newBody == null || newBody.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be null or empty");
        }
        if (newBody.length() > 1000) {
            throw new IllegalArgumentException("Message body cannot exceed 1000 characters");
        }
    }
}
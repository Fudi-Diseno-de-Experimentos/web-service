package synera.centralis.api.chat.domain.model.commands;

import java.util.UUID;

/**
 * Command to delete a message from the chat system.
 * This will mark the message as deleted.
 */
public record DeleteMessageCommand(
        UUID messageId
) {
    public DeleteMessageCommand {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
    }
}
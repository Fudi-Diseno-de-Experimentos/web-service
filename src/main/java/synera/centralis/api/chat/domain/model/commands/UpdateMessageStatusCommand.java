package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;

import java.util.UUID;

/**
 * Command to update the status of a message.
 * Used to mark messages as edited or deleted.
 */
public record UpdateMessageStatusCommand(
        UUID messageId,
        MessageStatus newStatus
) {
    public UpdateMessageStatusCommand {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("Message status cannot be null");
        }
    }
}
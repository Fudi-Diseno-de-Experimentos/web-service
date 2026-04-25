package synera.centralis.api.chat.domain.model.commands;

import java.util.UUID;

/**
 * Command to delete a chat image.
 */
public record DeleteChatImageCommand(UUID imageId) {
    public DeleteChatImageCommand {
        if (imageId == null) {
            throw new IllegalArgumentException("Image ID cannot be null");
        }
    }
}
package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Command to create a new chat image.
 */
public record CreateChatImageCommand(UUID groupId, UserId senderId, String imageUrl) {
    public CreateChatImageCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
    }
}
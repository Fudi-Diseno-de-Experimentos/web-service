package synera.centralis.api.chat.domain.services;

import synera.centralis.api.chat.domain.model.commands.CreateChatImageCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteChatImageCommand;
import synera.centralis.api.chat.domain.model.entities.ChatImage;

import java.util.Optional;

/**
 * Domain service interface for handling chat image-related commands.
 * Defines the contract for all chat image command operations.
 */
public interface ChatImageCommandService {

    /**
     * Handles creating a new chat image.
     * @param command the create chat image command
     * @return the created chat image or empty if creation failed
     */
    Optional<ChatImage> handle(CreateChatImageCommand command);

    /**
     * Handles deleting a chat image.
     * @param command the delete chat image command
     * @return the deleted chat image or empty if deletion failed
     */
    Optional<ChatImage> handle(DeleteChatImageCommand command);
}
package synera.centralis.api.chat.domain.services;

import synera.centralis.api.chat.domain.model.entities.ChatImage;
import synera.centralis.api.chat.domain.model.queries.GetChatImageByIdQuery;
import synera.centralis.api.chat.domain.model.queries.GetChatImagesByGroupIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for handling chat image-related queries.
 * Defines the contract for all chat image query operations.
 */
public interface ChatImageQueryService {

    /**
     * Handles retrieving a chat image by its ID.
     * @param query the get chat image by ID query
     * @return the chat image or empty if not found
     */
    Optional<ChatImage> handle(GetChatImageByIdQuery query);

    /**
     * Handles retrieving all chat images for a specific group.
     * @param query the get chat images by group ID query
     * @return list of chat images in the group
     */
    List<ChatImage> handle(GetChatImagesByGroupIdQuery query);
}
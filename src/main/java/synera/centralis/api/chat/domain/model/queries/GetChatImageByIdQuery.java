package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

/**
 * Query to get a chat image by its ID.
 */
public record GetChatImageByIdQuery(UUID imageId) {
    public GetChatImageByIdQuery {
        if (imageId == null) {
            throw new IllegalArgumentException("Image ID cannot be null");
        }
    }
}
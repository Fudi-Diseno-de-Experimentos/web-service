package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

/**
 * Query to get all chat images by group ID.
 */
public record GetChatImagesByGroupIdQuery(UUID groupId) {
    public GetChatImagesByGroupIdQuery {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
    }
}
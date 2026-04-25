package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve all messages from a specific group.
 * Messages should be ordered by creation time.
 */
public record GetMessagesByGroupIdQuery(
        UUID groupId
) {
    public GetMessagesByGroupIdQuery {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
    }
}
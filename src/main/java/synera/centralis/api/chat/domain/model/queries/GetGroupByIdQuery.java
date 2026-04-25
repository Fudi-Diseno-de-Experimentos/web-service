package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve a specific group by its ID.
 */
public record GetGroupByIdQuery(
        UUID groupId
) {
    public GetGroupByIdQuery {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
    }
}
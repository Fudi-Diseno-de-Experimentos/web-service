package synera.centralis.api.announcement.domain.model.queries;

import java.util.UUID;

/**
 * Get Comment By Id Query
 * Represents a request to retrieve a comment by its ID
 */
public record GetCommentByIdQuery(UUID commentId) {
    public GetCommentByIdQuery {
        if (commentId == null) {
            throw new IllegalArgumentException("Comment ID cannot be null");
        }
    }
}
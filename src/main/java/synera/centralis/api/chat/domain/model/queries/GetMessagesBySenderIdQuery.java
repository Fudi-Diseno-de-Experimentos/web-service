package synera.centralis.api.chat.domain.model.queries;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

/**
 * Query to retrieve all messages sent by a specific user.
 * Useful for user activity tracking and moderation.
 */
public record GetMessagesBySenderIdQuery(
        UserId senderId
) {
    public GetMessagesBySenderIdQuery {
        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
    }
}
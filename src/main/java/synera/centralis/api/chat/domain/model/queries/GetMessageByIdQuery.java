package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve a specific message by its ID.
 */
public record GetMessageByIdQuery(
        UUID messageId
) {
    public GetMessageByIdQuery {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
    }
}
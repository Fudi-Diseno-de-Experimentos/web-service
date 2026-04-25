package synera.centralis.api.event.domain.model.queries;

import synera.centralis.api.event.domain.model.valueobjects.UserId;

/**
 * Query to get all events for a specific user (as recipient).
 */
public record GetEventsByRecipientIdQuery(UserId recipientId) {
    public GetEventsByRecipientIdQuery {
        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null");
        }
    }
}


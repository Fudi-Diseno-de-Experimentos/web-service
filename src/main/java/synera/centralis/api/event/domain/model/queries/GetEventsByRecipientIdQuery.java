package synera.centralis.api.event.domain.model.queries;

import synera.centralis.api.event.domain.model.valueobjects.UserId;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to get all events for a specific user (as recipient).
 */
public record GetEventsByRecipientIdQuery(UserId recipientId, CompanyId companyId) {
    public GetEventsByRecipientIdQuery {
        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}


package synera.centralis.api.event.domain.model.queries;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to get an event by its ID.
 */
public record GetEventByIdQuery(UUID eventId, CompanyId companyId) {
    public GetEventByIdQuery {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}

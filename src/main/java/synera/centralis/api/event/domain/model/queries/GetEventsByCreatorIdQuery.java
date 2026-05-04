package synera.centralis.api.event.domain.model.queries;

import synera.centralis.api.event.domain.model.valueobjects.UserId;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to get all events created by a specific user (manager).
 */
public record GetEventsByCreatorIdQuery(UserId creatorId, CompanyId companyId) {
    public GetEventsByCreatorIdQuery {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}


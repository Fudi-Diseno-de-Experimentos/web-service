package synera.centralis.api.event.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to get all events in the system.
 */
public record GetAllEventsQuery(CompanyId companyId) {
}


package synera.centralis.api.company.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to retrieve all spaces belonging to a company.
 */
public record GetAllSpacesQuery(CompanyId companyId) {
}

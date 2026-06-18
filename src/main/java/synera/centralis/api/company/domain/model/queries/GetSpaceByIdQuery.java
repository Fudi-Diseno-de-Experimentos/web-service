package synera.centralis.api.company.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

/**
 * Query to retrieve a single space by ID, scoped to a company.
 */
public record GetSpaceByIdQuery(UUID spaceId, CompanyId companyId) {
}

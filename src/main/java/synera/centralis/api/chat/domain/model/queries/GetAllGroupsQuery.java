package synera.centralis.api.chat.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to retrieve all groups in the system.
 * Should be used with pagination in production scenarios.
 */
public record GetAllGroupsQuery(CompanyId companyId) {
}
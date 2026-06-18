package synera.centralis.api.announcement.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Get All Announcements Query
 * Represents a request to retrieve all announcements
 */
public record GetAllAnnouncementsQuery(CompanyId companyId) {
}
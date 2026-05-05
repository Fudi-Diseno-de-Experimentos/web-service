package synera.centralis.api.announcement.domain.model.queries;

import java.util.UUID;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Get Announcements By Creator Query
 * Represents a request to retrieve announcements created by a specific user
 */
public record GetAnnouncementsByCreatorQuery(UUID createdBy, CompanyId companyId) {
    public GetAnnouncementsByCreatorQuery {
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy must be a valid user ID");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
package synera.centralis.api.announcement.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import java.util.UUID;

/**
 * Get Announcement By Id Query
 * Represents a request to retrieve an announcement by its ID
 */
public record GetAnnouncementByIdQuery(UUID announcementId, CompanyId companyId) {
    public GetAnnouncementByIdQuery {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
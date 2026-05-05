package synera.centralis.api.announcement.domain.model.queries;

import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Get Announcements By Priority Query
 * Represents a request to retrieve announcements filtered by priority level
 */
public record GetAnnouncementsByPriorityQuery(Priority.PriorityLevel priorityLevel, CompanyId companyId) {
    public GetAnnouncementsByPriorityQuery {
        if (priorityLevel == null) {
            throw new IllegalArgumentException("Priority level cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
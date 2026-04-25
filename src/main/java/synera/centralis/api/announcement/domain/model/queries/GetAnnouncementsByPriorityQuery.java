package synera.centralis.api.announcement.domain.model.queries;

import synera.centralis.api.announcement.domain.model.valueobjects.Priority;

/**
 * Get Announcements By Priority Query
 * Represents a request to retrieve announcements filtered by priority level
 */
public record GetAnnouncementsByPriorityQuery(Priority.PriorityLevel priorityLevel) {
    public GetAnnouncementsByPriorityQuery {
        if (priorityLevel == null) {
            throw new IllegalArgumentException("Priority level cannot be null");
        }
    }
}
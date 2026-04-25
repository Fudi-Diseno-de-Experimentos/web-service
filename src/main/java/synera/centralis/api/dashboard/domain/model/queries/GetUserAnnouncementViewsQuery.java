package synera.centralis.api.dashboard.domain.model.queries;

import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;

/**
 * Query to get all announcements viewed by a specific user
 */
public record GetUserAnnouncementViewsQuery(UserId userId) {
    
    public GetUserAnnouncementViewsQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
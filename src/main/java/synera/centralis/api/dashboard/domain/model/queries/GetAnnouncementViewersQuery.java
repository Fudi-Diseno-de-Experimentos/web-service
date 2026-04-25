package synera.centralis.api.dashboard.domain.model.queries;

import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;

/**
 * Query to get all users who viewed a specific announcement
 */
public record GetAnnouncementViewersQuery(AnnouncementId announcementId) {
    
    public GetAnnouncementViewersQuery {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }
}
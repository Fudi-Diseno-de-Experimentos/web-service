package synera.centralis.api.dashboard.domain.model.queries;

import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;

/**
 * Query to get statistics for pie chart visualization of an announcement
 */
public record GetAnnouncementStatsQuery(AnnouncementId announcementId) {
    
    public GetAnnouncementStatsQuery {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }
}
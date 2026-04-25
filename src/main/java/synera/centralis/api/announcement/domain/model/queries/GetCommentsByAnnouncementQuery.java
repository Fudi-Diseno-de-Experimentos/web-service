package synera.centralis.api.announcement.domain.model.queries;

import java.util.UUID;

/**
 * Get Comments By Announcement Query
 * Represents a request to retrieve all comments for a specific announcement
 */
public record GetCommentsByAnnouncementQuery(UUID announcementId) {
    public GetCommentsByAnnouncementQuery {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }
}
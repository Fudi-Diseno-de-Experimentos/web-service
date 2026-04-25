package synera.centralis.api.announcement.domain.model.queries;

import java.util.UUID;

/**
 * Get Announcement By Id Query
 * Represents a request to retrieve an announcement by its ID
 */
public record GetAnnouncementByIdQuery(UUID announcementId) {
    public GetAnnouncementByIdQuery {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }
}
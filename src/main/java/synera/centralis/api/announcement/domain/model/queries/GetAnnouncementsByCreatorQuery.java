package synera.centralis.api.announcement.domain.model.queries;

import java.util.UUID;

/**
 * Get Announcements By Creator Query
 * Represents a request to retrieve announcements created by a specific user
 */
public record GetAnnouncementsByCreatorQuery(UUID createdBy) {
    public GetAnnouncementsByCreatorQuery {
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy must be a valid user ID");
        }
    }
}
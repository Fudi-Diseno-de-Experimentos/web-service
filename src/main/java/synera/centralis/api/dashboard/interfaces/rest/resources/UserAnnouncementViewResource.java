package synera.centralis.api.dashboard.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Resource representing a user's announcement view
 */
public record UserAnnouncementViewResource(
        String viewId,
        String announcementId,
        String announcementTitle,
        String announcementContent,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime viewedAt,
        String userId,
        String userFullName
) {
}
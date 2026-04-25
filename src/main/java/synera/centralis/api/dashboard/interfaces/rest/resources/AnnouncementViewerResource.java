package synera.centralis.api.dashboard.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Resource representing an announcement viewer
 */
public record AnnouncementViewerResource(
        String viewId,
        String userId,
        String userFullName,
        String userEmail,
        String userDepartment,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime viewedAt,
        String announcementId,
        String announcementTitle
) {
}
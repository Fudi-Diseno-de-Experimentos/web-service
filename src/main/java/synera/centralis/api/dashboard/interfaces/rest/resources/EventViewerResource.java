package synera.centralis.api.dashboard.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Resource representing an event viewer
 */
public record EventViewerResource(
        String viewId,
        String userId,
        String userFullName,
        String userEmail,
        String userDepartment,
        String userPosition,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime viewedAt,
        String eventId,
        String eventTitle
) {
}
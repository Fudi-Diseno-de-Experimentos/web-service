package synera.centralis.api.dashboard.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Resource representing a user's event view
 */
public record UserEventViewResource(
        String viewId,
        String eventId,
        String eventTitle,
        String eventDescription,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime eventDate,
        String eventLocation,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime viewedAt,
        String userId,
        String userFullName
) {
}
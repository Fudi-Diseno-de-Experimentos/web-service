package synera.centralis.api.dashboard.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Resource for view registration response
 */
public record ViewRegistrationResponseResource(
        String viewId,
        String userId,
        String contentId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime viewedAt,
        String message,
        boolean isNewView
) {
}
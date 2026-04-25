package synera.centralis.api.notification.domain.model.queries;

import java.util.UUID;

/**
 * Query to get a specific notification by its ID
 */
public record GetNotificationByIdQuery(UUID notificationId) {
    public GetNotificationByIdQuery {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
    }
}
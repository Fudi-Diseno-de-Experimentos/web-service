package synera.centralis.api.notification.domain.model.queries;

import java.util.UUID;

public record GetNotificationStatusQuery(
        UUID notificationId
) {
    public GetNotificationStatusQuery {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
    }
}
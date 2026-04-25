package synera.centralis.api.notification.domain.model.commands;

import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;

import java.util.UUID;

public record UpdateNotificationStatusCommand(
        UUID notificationId,
        NotificationStatus status
) {
    public UpdateNotificationStatusCommand {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
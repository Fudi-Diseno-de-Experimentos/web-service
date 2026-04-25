package synera.centralis.api.notification.domain.model.commands;

import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;

import java.util.List;

public record CreateNotificationCommand(
        String title,
        String message,
        List<String> recipients,
        NotificationPriority priority
) {
    public CreateNotificationCommand {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("Recipients list cannot be null or empty");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
    }
}
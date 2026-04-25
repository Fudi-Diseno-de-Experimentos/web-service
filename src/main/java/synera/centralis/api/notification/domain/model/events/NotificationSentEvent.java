package synera.centralis.api.notification.domain.model.events;

import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationSentEvent {
    
    private final UUID notificationId;
    private final NotificationStatus status;
    private final LocalDateTime occurredOn;
    
    public NotificationSentEvent(UUID notificationId, NotificationStatus status) {
        this.notificationId = notificationId;
        this.status = status;
        this.occurredOn = LocalDateTime.now();
    }
    
    public UUID getNotificationId() {
        return notificationId;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
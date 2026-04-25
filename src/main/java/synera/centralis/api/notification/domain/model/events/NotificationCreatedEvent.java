package synera.centralis.api.notification.domain.model.events;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class NotificationCreatedEvent {
    
    private final UUID notificationId;
    private final String title;
    private final String message;
    private final List<String> recipients;
    private final LocalDateTime occurredOn;
    
    public NotificationCreatedEvent(UUID notificationId, String title, String message, List<String> recipients) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.recipients = recipients;
        this.occurredOn = LocalDateTime.now();
    }
    
    public UUID getNotificationId() {
        return notificationId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public List<String> getRecipients() {
        return recipients;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
package synera.centralis.api.shared.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when an urgent announcement is created.
 * This event triggers notifications to all users in the system.
 */
public record UrgentAnnouncementCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID announcementId,
        String title,
        String content,
        UUID createdBy
) implements DomainEvent {
    
    /**
     * Creates a new UrgentAnnouncementCreatedEvent with current timestamp.
     */
    public static UrgentAnnouncementCreatedEvent create(
            UUID announcementId,
            String title,
            String content,
            UUID createdBy) {
        return new UrgentAnnouncementCreatedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                announcementId,
                title,
                content,
                createdBy
        );
    }
}
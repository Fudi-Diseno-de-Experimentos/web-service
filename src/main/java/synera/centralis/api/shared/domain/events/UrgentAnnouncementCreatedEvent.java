package synera.centralis.api.shared.domain.events;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when an urgent announcement is created.
 * Carries the announcing company so notifications stay scoped to that company's
 * users instead of fanning out across every tenant.
 */
public record UrgentAnnouncementCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID announcementId,
        String title,
        String content,
        UUID createdBy,
        CompanyId companyId
) implements DomainEvent {

    /**
     * Creates a new UrgentAnnouncementCreatedEvent with current timestamp.
     */
    public static UrgentAnnouncementCreatedEvent create(
            UUID announcementId,
            String title,
            String content,
            UUID createdBy,
            CompanyId companyId) {
        return new UrgentAnnouncementCreatedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                announcementId,
                title,
                content,
                createdBy,
                companyId
        );
    }
}
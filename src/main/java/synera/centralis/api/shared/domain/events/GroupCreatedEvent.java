package synera.centralis.api.shared.domain.events;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Domain event fired when a new group is created.
 * This event triggers notifications to all group members except the creator.
 */
public record GroupCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID groupId,
        String groupName,
        UUID createdBy,
        Set<UUID> memberIds
) implements DomainEvent {
    
    /**
     * Creates a new GroupCreatedEvent with current timestamp.
     */
    public static GroupCreatedEvent create(
            UUID groupId,
            String groupName,
            UUID createdBy,
            Set<UUID> memberIds) {
        return new GroupCreatedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                groupId,
                groupName,
                createdBy,
                memberIds
        );
    }
}
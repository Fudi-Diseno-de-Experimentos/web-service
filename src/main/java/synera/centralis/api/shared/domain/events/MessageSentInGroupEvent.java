package synera.centralis.api.shared.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when a message is sent in a group.
 * This event triggers notifications to all group members except the sender.
 */
public record MessageSentInGroupEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID messageId,
        UUID groupId,
        String groupName,
        UUID senderId,
        String messageContent
) implements DomainEvent {
    
    /**
     * Creates a new MessageSentInGroupEvent with current timestamp.
     */
    public static MessageSentInGroupEvent create(
            UUID messageId,
            UUID groupId,
            String groupName,
            UUID senderId,
            String messageContent) {
        return new MessageSentInGroupEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                messageId,
                groupId,
                groupName,
                senderId,
                messageContent
        );
    }
}
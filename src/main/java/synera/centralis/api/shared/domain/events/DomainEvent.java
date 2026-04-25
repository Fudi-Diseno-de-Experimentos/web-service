package synera.centralis.api.shared.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base interface for all domain events in the system.
 * Domain events represent something important that happened in the domain
 * and should be communicated to other parts of the system.
 */
public interface DomainEvent {
    
    /**
     * Unique identifier for this event instance.
     * @return the event ID
     */
    UUID eventId();
    
    /**
     * Timestamp when the event occurred.
     * @return the occurrence time
     */
    LocalDateTime occurredAt();
}
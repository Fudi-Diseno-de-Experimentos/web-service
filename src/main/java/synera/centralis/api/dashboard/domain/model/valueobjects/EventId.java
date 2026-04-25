package synera.centralis.api.dashboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Event ID value object from event context
 */
@Embeddable
public record EventId(UUID value) {
    
    public EventId {
        if (value == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }

    public EventId(String value) {
        this(UUID.fromString(value));
    }
}
package synera.centralis.api.dashboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Content View ID value object representing a unique content view identifier
 */
@Embeddable
public record ContentViewId(UUID value) {
    
    public ContentViewId {
        if (value == null) {
            throw new IllegalArgumentException("Content view ID cannot be null");
        }
    }

    public ContentViewId() {
        this(UUID.randomUUID());
    }

    public ContentViewId(String value) {
        this(UUID.fromString(value));
    }
}
package synera.centralis.api.dashboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * User ID value object from IAM context
 */
@Embeddable
public record UserId(UUID value) {
    
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }

    public UserId(String value) {
        this(UUID.fromString(value));
    }
}
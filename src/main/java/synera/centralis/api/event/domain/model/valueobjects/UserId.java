package synera.centralis.api.event.domain.model.valueobjects;

import java.util.UUID;

import jakarta.persistence.Embeddable;

/**
 * Value object representing User ID from IAM context
 */
@Embeddable
public record UserId(UUID userId) {
    public UserId {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}


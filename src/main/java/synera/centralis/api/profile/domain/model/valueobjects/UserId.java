package synera.centralis.api.profile.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.util.UUID;

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
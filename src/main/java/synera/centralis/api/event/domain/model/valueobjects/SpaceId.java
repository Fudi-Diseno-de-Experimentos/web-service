package synera.centralis.api.event.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.util.UUID;

/**
 * Reference to a company Space (room) from within the event context.
 * Embedded by value in {@code Event} instead of a JPA association; the Space
 * aggregate itself is identified by the {@code id} of its abstract aggregate root.
 */
@Embeddable
public record SpaceId(UUID spaceId) {
    public SpaceId {
        if (spaceId == null) {
            throw new IllegalArgumentException("SpaceId cannot be null");
        }
    }
}

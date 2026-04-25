package synera.centralis.api.dashboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Announcement ID value object from announcement context
 */
@Embeddable
public record AnnouncementId(UUID value) {
    
    public AnnouncementId {
        if (value == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }

    public AnnouncementId(String value) {
        this(UUID.fromString(value));
    }
}
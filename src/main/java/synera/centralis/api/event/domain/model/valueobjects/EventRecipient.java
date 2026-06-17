package synera.centralis.api.event.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a single recipient of an event together with their
 * invitation response {@link RecipientStatus}. Stored as an element of the
 * {@code event_recipients} collection table.
 *
 * <p>Identity is the {@code userId} alone — two recipients with the same user are
 * considered equal regardless of status, so a {@code Set<EventRecipient>} holds at
 * most one row per user and status changes mutate in place.</p>
 */
@Embeddable
public class EventRecipient {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RecipientStatus status;

    protected EventRecipient() {
        // Required by JPA
    }

    public EventRecipient(UUID userId, RecipientStatus status) {
        if (userId == null) {
            throw new IllegalArgumentException("Recipient user ID cannot be null");
        }
        this.userId = userId;
        this.status = status == null ? RecipientStatus.PENDING : status;
    }

    /** Creates a recipient with the default {@link RecipientStatus#PENDING} status. */
    public EventRecipient(UUID userId) {
        this(userId, RecipientStatus.PENDING);
    }

    public UUID getUserId() {
        return userId;
    }

    public RecipientStatus getStatus() {
        return status;
    }

    /** Null-safe status: a legacy {@code null} (pre-backfill) reads as {@link RecipientStatus#PENDING}. */
    public RecipientStatus effectiveStatus() {
        return status == null ? RecipientStatus.PENDING : status;
    }

    public void setStatus(RecipientStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Recipient status cannot be null");
        }
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventRecipient that)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}

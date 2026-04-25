package synera.centralis.api.event.domain.model.agreggates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Event aggregate root representing a business event.
 * Contains event information and recipient management capabilities.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event extends AuditableAbstractAggregateRoot<Event> {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "location", length = 500)
    private String location;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "created_by"))
    private UserId createdBy;

    @ElementCollection
    @CollectionTable(name = "event_recipients", joinColumns = @JoinColumn(name = "event_id"))
    @AttributeOverride(name = "userId", column = @Column(name = "user_id"))
    private Set<UserId> recipients = new HashSet<>();

    /**
     * Constructor for creating a new event.
     */
    public Event(String title, String description, LocalDateTime date, String location,
                 List<UUID> recipientIds, UserId createdBy) {
        this.title = validateAndSetTitle(title);
        this.description = validateAndSetDescription(description);
        this.date = validateDate(date);
        this.location = validateAndSetLocation(location);
        this.createdBy = validateCreatedBy(createdBy);
        this.recipients = new HashSet<>();

        // Add recipients
        if (recipientIds != null && !recipientIds.isEmpty()) {
            recipientIds.forEach(recipientId -> this.recipients.add(new UserId(recipientId)));
        }

        validateAtLeastOneRecipient();
    }

    /**
     * Updates event information.
     */
    public void updateEvent(String title, String description, LocalDateTime date,
                           String location, List<UUID> recipientIds) {
        if (title != null) {
            this.title = validateAndSetTitle(title);
        }
        if (description != null) {
            this.description = validateAndSetDescription(description);
        }
        if (date != null) {
            this.date = validateDate(date);
        }
        if (location != null) {
            this.location = validateAndSetLocation(location);
        }
        if (recipientIds != null) {
            this.recipients.clear();
            recipientIds.forEach(recipientId -> this.recipients.add(new UserId(recipientId)));
            validateAtLeastOneRecipient();
        }
    }

    /**
     * Adds a recipient to the event.
     */
    public void addRecipient(UserId recipientId) {
        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null");
        }
        this.recipients.add(recipientId);
    }

    /**
     * Removes a recipient from the event.
     */
    public void removeRecipient(UserId recipientId) {
        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null");
        }
        this.recipients.remove(recipientId);
    }

    /**
     * Checks if a user is a recipient of this event.
     */
    public boolean isRecipient(UserId userId) {
        return this.recipients.contains(userId);
    }

    /**
     * Gets the number of recipients.
     */
    public int getRecipientCount() {
        return this.recipients.size();
    }

    // Validation methods
    private String validateAndSetTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be null or empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Event title cannot exceed 200 characters");
        }
        return title.trim();
    }

    private String validateAndSetDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be null or empty");
        }
        if (description.length() > 1000) {
            throw new IllegalArgumentException("Event description cannot exceed 1000 characters");
        }
        return description.trim();
    }

    private LocalDateTime validateDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        return date;
    }

    private String validateAndSetLocation(String location) {
        if (location != null) {
            if (location.length() > 500) {
                throw new IllegalArgumentException("Event location cannot exceed 500 characters");
            }
            return location.trim().isEmpty() ? null : location.trim();
        }
        return null;
    }

    private UserId validateCreatedBy(UserId createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }
        return createdBy;
    }

    private void validateAtLeastOneRecipient() {
        if (this.recipients.isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one recipient");
        }
    }
}


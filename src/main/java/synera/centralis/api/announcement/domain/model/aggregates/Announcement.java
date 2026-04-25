package synera.centralis.api.announcement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.UUID;

/**
 * Announcement Aggregate Root
 * Represents an announcement in the system
 */
@Entity
@Table(name = "announcements")
@Getter
@NoArgsConstructor
public class Announcement extends AuditableAbstractAggregateRoot<Announcement> {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    @Embedded
    @AttributeOverride(name = "level", column = @Column(name = "priority"))
    private Priority priority;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    /**
     * Constructor for creating a new announcement
     */
    public Announcement(String title, String description, String image, Priority priority, UUID createdBy) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy must be a valid user ID");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
        if (description.trim().length() > 5000) {
            throw new IllegalArgumentException("Description cannot exceed 5000 characters");
        }
        if (image != null && image.trim().length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }

        this.title = title.trim();
        this.description = description.trim();
        this.image = image != null ? image.trim() : null;
        this.priority = priority;
        this.createdBy = createdBy;
    }

    /**
     * Updates the announcement information
     */
    public void update(String title, String description, String image, Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
        if (description.trim().length() > 5000) {
            throw new IllegalArgumentException("Description cannot exceed 5000 characters");
        }
        if (image != null && image.trim().length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }

        this.title = title.trim();
        this.description = description.trim();
        this.image = image != null ? image.trim() : null;
        this.priority = priority;
    }

    /**
     * Updates only the priority
     */
    public void updatePriority(Priority newPriority) {
        if (newPriority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        this.priority = newPriority;
    }

    /**
     * Checks if announcement is high priority or urgent
     */
    public boolean isHighPriorityOrUrgent() {
        return priority.isHighOrUrgent();
    }

    /**
     * Checks if announcement is urgent
     */
    public boolean isUrgent() {
        return priority.isUrgent();
    }
}

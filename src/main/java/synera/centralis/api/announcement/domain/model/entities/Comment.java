package synera.centralis.api.announcement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.UUID;

/**
 * Comment Entity
 * Represents a comment made on an announcement
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment extends AuditableAbstractAggregateRoot<Comment> {

    @Column(name = "announcement_id", nullable = false)
    private UUID announcementId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Constructor for creating a new comment
     */
    public Comment(UUID announcementId, UUID employeeId, String content) {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID must be a valid user ID");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be null or empty");
        }
        if (content.trim().length() > 1000) {
            throw new IllegalArgumentException("Comment content cannot exceed 1000 characters");
        }

        this.announcementId = announcementId;
        this.employeeId = employeeId;
        this.content = content.trim();
    }

    /**
     * Updates the comment content
     */
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be null or empty");
        }
        if (newContent.trim().length() > 1000) {
            throw new IllegalArgumentException("Comment content cannot exceed 1000 characters");
        }
        this.content = newContent.trim();
    }
}

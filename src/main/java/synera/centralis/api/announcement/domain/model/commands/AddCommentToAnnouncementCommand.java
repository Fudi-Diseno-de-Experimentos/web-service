package synera.centralis.api.announcement.domain.model.commands;

import java.util.UUID;

/**
 * Add Comment To Announcement Command
 * Represents the intention to add a comment to an announcement
 */
public record AddCommentToAnnouncementCommand(
    UUID announcementId,
    UUID employeeId,
    String content
) {
    public AddCommentToAnnouncementCommand {
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
    }
}
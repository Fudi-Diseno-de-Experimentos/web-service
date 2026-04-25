package synera.centralis.api.announcement.domain.model.commands;

import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import java.util.UUID;

/**
 * Create Announcement Command
 * Represents the intention to create a new announcement
 */
public record CreateAnnouncementCommand(
    String title,
    String description,
    String image,
    Priority priority,
    UUID createdBy
) {
    public CreateAnnouncementCommand {
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
    }
}
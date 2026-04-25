package synera.centralis.api.announcement.domain.model.commands;

import java.util.UUID;

/**
 * Delete Announcement Command
 * Represents the intention to delete an announcement
 */
public record DeleteAnnouncementCommand(UUID announcementId) {
    public DeleteAnnouncementCommand {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }
}
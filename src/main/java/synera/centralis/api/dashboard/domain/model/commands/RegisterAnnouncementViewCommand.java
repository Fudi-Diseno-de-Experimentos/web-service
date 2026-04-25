package synera.centralis.api.dashboard.domain.model.commands;

import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;

/**
 * Command to register an announcement view
 * Represents the intention to track that a user has viewed an announcement
 */
public record RegisterAnnouncementViewCommand(
        UserId userId,
        AnnouncementId announcementId
) {
    
    public RegisterAnnouncementViewCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
    }
}
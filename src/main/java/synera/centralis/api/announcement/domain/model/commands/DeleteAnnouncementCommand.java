package synera.centralis.api.announcement.domain.model.commands;

import java.util.UUID;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Delete Announcement Command
 * Represents the intention to delete an announcement
 */
public record DeleteAnnouncementCommand(UUID announcementId, CompanyId companyId) {
    public DeleteAnnouncementCommand {
        if (announcementId == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
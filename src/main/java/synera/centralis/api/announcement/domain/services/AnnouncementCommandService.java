package synera.centralis.api.announcement.domain.services;

import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.commands.CreateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.DeleteAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.UpdateAnnouncementCommand;

import java.util.Optional;
import java.util.UUID;

/**
 * Announcement Command Service
 * Defines the contract for announcement command operations
 */
public interface AnnouncementCommandService {

    /**
     * Handles the creation of a new announcement
     */
    Optional<Announcement> handle(CreateAnnouncementCommand command);

    /**
     * Handles the update of an existing announcement
     */
    Optional<Announcement> handle(UpdateAnnouncementCommand command);

    /**
     * Handles the deletion of an announcement
     */
    boolean handle(DeleteAnnouncementCommand command);

    /**
     * Checks if an announcement exists by ID
     */
    boolean existsById(UUID announcementId);
}
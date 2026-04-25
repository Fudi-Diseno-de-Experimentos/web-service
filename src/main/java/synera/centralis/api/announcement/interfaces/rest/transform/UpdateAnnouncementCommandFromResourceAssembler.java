package synera.centralis.api.announcement.interfaces.rest.transform;

import synera.centralis.api.announcement.domain.model.commands.UpdateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.announcement.interfaces.rest.resources.UpdateAnnouncementResource;

import java.util.UUID;

/**
 * Update Announcement Command From Resource Assembler
 * Transforms UpdateAnnouncementResource to UpdateAnnouncementCommand
 */
public class UpdateAnnouncementCommandFromResourceAssembler {

    /**
     * Transforms UpdateAnnouncementResource to UpdateAnnouncementCommand
     */
    public static UpdateAnnouncementCommand toCommandFromResource(UUID announcementId, UpdateAnnouncementResource resource) {
        var priorityLevel = Priority.PriorityLevel.valueOf(resource.priority().toUpperCase());
        var priority = new Priority(priorityLevel);
        
        return new UpdateAnnouncementCommand(
            announcementId,
            resource.title(),
            resource.description(),
            resource.image(),
            priority
        );
    }
}
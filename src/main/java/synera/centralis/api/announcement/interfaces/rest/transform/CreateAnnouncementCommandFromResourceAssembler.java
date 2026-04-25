package synera.centralis.api.announcement.interfaces.rest.transform;

import synera.centralis.api.announcement.domain.model.commands.CreateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.announcement.interfaces.rest.resources.CreateAnnouncementResource;

/**
 * Create Announcement Command From Resource Assembler
 * Transforms CreateAnnouncementResource to CreateAnnouncementCommand
 */
public class CreateAnnouncementCommandFromResourceAssembler {

    /**
     * Transforms CreateAnnouncementResource to CreateAnnouncementCommand
     */
    public static CreateAnnouncementCommand toCommandFromResource(CreateAnnouncementResource resource) {
        var priorityLevel = Priority.PriorityLevel.valueOf(resource.priority().toUpperCase());
        var priority = new Priority(priorityLevel);
        
        return new CreateAnnouncementCommand(
            resource.title(),
            resource.description(),
            resource.image(),
            priority,
            resource.createdBy()
        );
    }
}
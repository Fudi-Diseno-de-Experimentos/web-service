package synera.centralis.api.announcement.interfaces.rest.transform;

import synera.centralis.api.announcement.domain.model.commands.AddCommentToAnnouncementCommand;
import synera.centralis.api.announcement.interfaces.rest.resources.CreateCommentResource;

import java.util.UUID;

/**
 * Create Comment Command From Resource Assembler
 * Transforms CreateCommentResource to AddCommentToAnnouncementCommand
 */
public class CreateCommentCommandFromResourceAssembler {

    /**
     * Transforms CreateCommentResource to AddCommentToAnnouncementCommand
     */
    public static AddCommentToAnnouncementCommand toCommandFromResource(UUID announcementId, CreateCommentResource resource) {
        return new AddCommentToAnnouncementCommand(
            announcementId,
            resource.employeeId(),
            resource.content()
        );
    }
}
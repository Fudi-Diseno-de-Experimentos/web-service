package synera.centralis.api.announcement.interfaces.rest.transform;

import synera.centralis.api.announcement.domain.model.entities.Comment;
import synera.centralis.api.announcement.interfaces.rest.resources.CommentResource;

/**
 * Comment Resource From Entity Assembler
 * Transforms Comment entity to CommentResource
 */
public class CommentResourceFromEntityAssembler {

    /**
     * Transforms a Comment entity to CommentResource
     */
    public static CommentResource toResourceFromEntity(Comment entity) {
        return new CommentResource(
            entity.getId(),
            entity.getAnnouncementId(),
            entity.getEmployeeId(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
package synera.centralis.api.announcement.interfaces.rest.transform;

import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.interfaces.rest.resources.AnnouncementResource;

/**
 * Announcement Resource From Entity Assembler
 * Transforms Announcement entity to AnnouncementResource
 */
public class AnnouncementResourceFromEntityAssembler {

    /**
     * Transforms an Announcement entity to AnnouncementResource
     */
    public static AnnouncementResource toResourceFromEntity(Announcement entity) {
        return new AnnouncementResource(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getImage(),
            entity.getPriority().level().name(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
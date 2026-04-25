package synera.centralis.api.announcement.domain.services;

import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Announcement Query Service
 * Defines the contract for announcement query operations
 */
public interface AnnouncementQueryService {

    /**
     * Handles retrieving an announcement by ID
     */
    Optional<Announcement> handle(GetAnnouncementByIdQuery query);

    /**
     * Handles retrieving all announcements
     */
    List<Announcement> handle(GetAllAnnouncementsQuery query);

    /**
     * Handles retrieving announcements by priority
     */
    List<Announcement> handle(GetAnnouncementsByPriorityQuery query);

    /**
     * Handles retrieving announcements by creator
     */
    List<Announcement> handle(GetAnnouncementsByCreatorQuery query);
}
package synera.centralis.api.announcement.application.internal.queryservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.queries.*;
import synera.centralis.api.announcement.domain.services.AnnouncementQueryService;
import synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories.AnnouncementRepository;

import java.util.List;
import java.util.Optional;

/**
 * Announcement Query Service Implementation
 * Handles query operations for announcements
 */
@Service
public class AnnouncementQueryServiceImpl implements AnnouncementQueryService {

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementQueryServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Announcement> handle(GetAnnouncementByIdQuery query) {
        return announcementRepository.findById(query.announcementId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> handle(GetAllAnnouncementsQuery query) {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> handle(GetAnnouncementsByPriorityQuery query) {
        return announcementRepository.findByPriorityLevel(query.priorityLevel());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> handle(GetAnnouncementsByCreatorQuery query) {
        return announcementRepository.findByCreatedByOrderByCreatedAtDesc(query.createdBy());
    }
}
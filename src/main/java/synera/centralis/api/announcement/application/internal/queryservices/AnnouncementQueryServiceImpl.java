package synera.centralis.api.announcement.application.internal.queryservices;

import org.springframework.beans.factory.annotation.Autowired;
import synera.centralis.api.iam.infrastructure.authorization.sfs.utils.SecurityUtils;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
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
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (SecurityUtils.isAdmin() || currentCompanyId == null) {
            return announcementRepository.findById(query.announcementId());
        }
        return announcementRepository.findByIdAndCompanyId(query.announcementId(), currentCompanyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> handle(GetAllAnnouncementsQuery query) {
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (SecurityUtils.isAdmin() || currentCompanyId == null) {
            return announcementRepository.findAllByOrderByCreatedAtDesc();
        }
        return announcementRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentCompanyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> handle(GetAnnouncementsByPriorityQuery query) {
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (SecurityUtils.isAdmin() || currentCompanyId == null) {
            return announcementRepository.findByPriorityLevel(query.priorityLevel());
        }
        return announcementRepository.findByPriorityLevelAndCompanyId(query.priorityLevel(), currentCompanyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> handle(GetAnnouncementsByCreatorQuery query) {
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (SecurityUtils.isAdmin() || currentCompanyId == null) {
            return announcementRepository.findByCreatedByOrderByCreatedAtDesc(query.createdBy());
        }
        return announcementRepository.findByCreatedByAndCompanyIdOrderByCreatedAtDesc(query.createdBy(), currentCompanyId);
    }
}
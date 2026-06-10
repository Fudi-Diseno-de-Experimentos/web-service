package synera.centralis.api.announcement.application.internal.commandservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import synera.centralis.api.iam.infrastructure.authorization.sfs.utils.SecurityUtils;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.commands.CreateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.DeleteAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.UpdateAnnouncementCommand;
import synera.centralis.api.announcement.domain.services.AnnouncementCommandService;
import synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories.AnnouncementRepository;
import synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories.CommentRepository;
import synera.centralis.api.shared.domain.events.UrgentAnnouncementCreatedEvent;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;

import java.util.UUID;

/**
 * Announcement Command Service Implementation
 * Handles command operations for announcements
 */
@Slf4j
@Service
public class AnnouncementCommandServiceImpl implements AnnouncementCommandService {

    private final AnnouncementRepository announcementRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AnnouncementCommandServiceImpl(AnnouncementRepository announcementRepository,
                                        CommentRepository commentRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.announcementRepository = announcementRepository;
        this.commentRepository = commentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Announcement handle(CreateAnnouncementCommand command) {
        try {
            var announcement = new Announcement(
                command.title(),
                command.description(),
                command.image(),
                command.priority(),
                command.createdBy()
            );

            announcement.setCompanyId(command.companyId());

            var savedAnnouncement = announcementRepository.save(announcement);

            log.debug("Announcement created: title='{}', priority={}, urgent={}",
                    savedAnnouncement.getTitle(),
                    savedAnnouncement.getPriority().level(),
                    savedAnnouncement.getPriority().isUrgent());

            // Publish event if announcement is urgent
            if (savedAnnouncement.getPriority().isUrgent()) {
                var event = UrgentAnnouncementCreatedEvent.create(
                    savedAnnouncement.getId(),
                    savedAnnouncement.getTitle(),
                    savedAnnouncement.getDescription(),
                    savedAnnouncement.getCreatedBy(),
                    savedAnnouncement.getCompanyId()
                );

                eventPublisher.publishEvent(event);
                log.debug("Published UrgentAnnouncementCreatedEvent for announcement {}", savedAnnouncement.getId());
            }

            return savedAnnouncement;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating announcement", e);
            throw new ValidationException("Error creating announcement: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Announcement handle(UpdateAnnouncementCommand command) {
        var announcement = announcementRepository.findByIdAndCompanyId(command.announcementId(), command.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id: " + command.announcementId()));

        try {
            announcement.update(
                command.title(),
                command.description(),
                command.image(),
                command.priority()
            );
            return announcementRepository.save(announcement);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new ValidationException("Error updating announcement: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean handle(DeleteAnnouncementCommand command) {
        var announcement = announcementRepository.findByIdAndCompanyId(command.announcementId(), command.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id: " + command.announcementId()));

        // Delete all comments associated with this announcement first
        commentRepository.deleteByAnnouncementId(command.announcementId());
        
        // Delete the announcement
        announcementRepository.delete(announcement);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID announcementId) {
        return announcementRepository.existsById(announcementId);
    }
}
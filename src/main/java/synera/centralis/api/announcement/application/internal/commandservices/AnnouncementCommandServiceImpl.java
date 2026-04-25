package synera.centralis.api.announcement.application.internal.commandservices;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Announcement Command Service Implementation
 * Handles command operations for announcements
 */
@Service
public class AnnouncementCommandServiceImpl implements AnnouncementCommandService {

    private static final Logger logger = Logger.getLogger(AnnouncementCommandServiceImpl.class.getName());
    
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
    public Optional<Announcement> handle(CreateAnnouncementCommand command) {
        try {
            var announcement = new Announcement(
                command.title(),
                command.description(),
                command.image(),
                command.priority(),
                command.createdBy()
            );

            var savedAnnouncement = announcementRepository.save(announcement);
            
            logger.info("=== ANNOUNCEMENT CREATED ===");
            logger.info("Title: " + savedAnnouncement.getTitle());
            logger.info("Priority: " + savedAnnouncement.getPriority().level());
            logger.info("Is Urgent: " + savedAnnouncement.getPriority().isUrgent());
            
            // Publish event if announcement is urgent
            if (savedAnnouncement.getPriority().isUrgent()) {
                logger.info("🚨 PUBLISHING URGENT ANNOUNCEMENT EVENT for: " + savedAnnouncement.getTitle());
                
                var event = UrgentAnnouncementCreatedEvent.create(
                    savedAnnouncement.getId(),
                    savedAnnouncement.getTitle(),
                    savedAnnouncement.getDescription(),
                    savedAnnouncement.getCreatedBy()
                );
                
                logger.info("🚀 Event created: " + event.toString());
                eventPublisher.publishEvent(event);
                logger.info("✅ Event published successfully");
            } else {
                logger.info("ℹ️ Announcement is not urgent, no event will be published");
            }
            
            return Optional.of(savedAnnouncement);
        } catch (Exception e) {
            logger.severe("Error creating announcement: " + e.getMessage());
            throw new RuntimeException("Error creating announcement: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Optional<Announcement> handle(UpdateAnnouncementCommand command) {
        return announcementRepository.findById(command.announcementId())
                .map(announcement -> {
                    try {
                        announcement.update(
                            command.title(),
                            command.description(),
                            command.image(),
                            command.priority()
                        );
                        return announcementRepository.save(announcement);
                    } catch (Exception e) {
                        throw new RuntimeException("Error updating announcement: " + e.getMessage(), e);
                    }
                });
    }

    @Override
    @Transactional
    public boolean handle(DeleteAnnouncementCommand command) {
        return announcementRepository.findById(command.announcementId())
                .map(announcement -> {
                    // Delete all comments associated with this announcement first
                    commentRepository.deleteByAnnouncementId(command.announcementId());
                    
                    // Delete the announcement
                    announcementRepository.delete(announcement);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID announcementId) {
        return announcementRepository.existsById(announcementId);
    }
}
package synera.centralis.api.notification.application.internal.eventhandlers;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
import synera.centralis.api.notification.domain.services.NotificationCommandService;
import synera.centralis.api.shared.domain.events.UrgentAnnouncementCreatedEvent;

import java.util.logging.Logger;

/**
 * Event handler for urgent announcement created events.
 * Notifies all registered users when an urgent announcement is created.
 */
@Component
public class UrgentAnnouncementNotificationHandler {
    
    private static final Logger logger = Logger.getLogger(UrgentAnnouncementNotificationHandler.class.getName());
    
    private final NotificationCommandService notificationCommandService;
    private final UserRepository userRepository;
    
    public UrgentAnnouncementNotificationHandler(
            NotificationCommandService notificationCommandService, 
            UserRepository userRepository) {
        this.notificationCommandService = notificationCommandService;
        this.userRepository = userRepository;
    }
    
    /**
     * Handles urgent announcement created events by creating notifications for all users
     * @param event The urgent announcement created event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(UrgentAnnouncementCreatedEvent event) {
        logger.info("🎯 EVENT HANDLER TRIGGERED: UrgentAnnouncementNotificationHandler");
        logger.info("📢 Processing urgent announcement notification for: " + event.title());
        logger.info("📝 Content: " + event.content());
        logger.info("👤 Created by: " + event.createdBy());
        
        try {
            // Get all user UUIDs to notify
            var allUsers = userRepository.findAll();
            logger.info("👥 Found " + allUsers.size() + " users in database");
            
            var allUserIds = allUsers.stream()
                    .map(user -> user.getId().toString()) // Use User entity ID (UUID)
                    .toList();
            
            logger.info("📋 User UUIDs to notify: " + allUserIds);
            
            if (allUserIds.isEmpty()) {
                logger.warning("⚠️ No users found to notify for urgent announcement: " + event.title());
                return;
            }
            
            // Create notification command
            var command = new CreateNotificationCommand(
                    "Urgent: " + event.title(),
                    event.content(),
                    allUserIds,
                    NotificationPriority.HIGH
            );
            
            logger.info("📤 Creating notification command: " + command.title());
            
            // Send notification
            var result = notificationCommandService.handle(command);
            
            if (result.isPresent()) {
                logger.info("✅ Successfully created urgent announcement notification for " + 
                           allUserIds.size() + " users. Notification ID: " + result.get().getId());
            } else {
                logger.severe("❌ Failed to create urgent announcement notification");
            }
            
        } catch (Exception e) {
            logger.severe("💥 Error processing urgent announcement notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
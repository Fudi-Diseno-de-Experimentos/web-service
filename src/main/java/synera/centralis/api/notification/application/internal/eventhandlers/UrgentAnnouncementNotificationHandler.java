package synera.centralis.api.notification.application.internal.eventhandlers;

import lombok.extern.slf4j.Slf4j;
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

/**
 * Event handler for urgent announcement created events.
 * Notifies all registered users when an urgent announcement is created.
 */
@Slf4j
@Component
public class UrgentAnnouncementNotificationHandler {

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
        log.info("🎯 EVENT HANDLER TRIGGERED: UrgentAnnouncementNotificationHandler");
        log.info("📢 Processing urgent announcement notification for: " + event.title());
        log.info("📝 Content: " + event.content());
        log.info("👤 Created by: " + event.createdBy());

        try {
            // Get all user UUIDs to notify
            var allUsers = userRepository.findAll();
            log.info("👥 Found " + allUsers.size() + " users in database");

            var allUserIds = allUsers.stream()
                    .map(user -> user.getId().toString()) // Use User entity ID (UUID)
                    .toList();

            log.info("📋 User UUIDs to notify: " + allUserIds);

            if (allUserIds.isEmpty()) {
                log.warn("⚠️ No users found to notify for urgent announcement: " + event.title());
                return;
            }

            // Create notification command
            var command = new CreateNotificationCommand(
                    "Urgent: " + event.title(),
                    event.content(),
                    allUserIds,
                    NotificationPriority.HIGH
            );

            log.info("📤 Creating notification command: " + command.title());

            // Send notification
            var result = notificationCommandService.handle(command);

            if (result.isPresent()) {
                log.info("✅ Successfully created urgent announcement notification for " +
                           allUserIds.size() + " users. Notification ID: " + result.get().getId());
            } else {
                log.error("❌ Failed to create urgent announcement notification");
            }

        } catch (Exception e) {
            log.error("💥 Error processing urgent announcement notification", e);
        }
    }
}

package synera.centralis.api.notification.application.internal.eventhandlers;

import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.GroupRepository;
import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
import synera.centralis.api.notification.domain.services.NotificationCommandService;
import synera.centralis.api.shared.domain.events.GroupCreatedEvent;

/**
 * Event handler for group created events.
 * Notifies all group members except the creator when a new group is created.
 */
@Component
public class GroupCreationNotificationHandler {
    
    private static final Logger logger = Logger.getLogger(GroupCreationNotificationHandler.class.getName());
    
    private final NotificationCommandService notificationCommandService;
    private final GroupRepository groupRepository;
    
    public GroupCreationNotificationHandler(
            NotificationCommandService notificationCommandService,
            GroupRepository groupRepository) {
        this.notificationCommandService = notificationCommandService;
        this.groupRepository = groupRepository;
    }
    
    /**
     * Handles group created events by creating notifications for group members except creator
     * @param event The group created event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(GroupCreatedEvent event) {
        logger.info("🎯 EVENT HANDLER TRIGGERED: GroupCreationNotificationHandler");
        logger.info("👥 Processing group creation notification for group: " + event.groupId() + 
                   ", creator: " + event.createdBy());
        
        try {
            // Find the newly created group with members eagerly loaded
            var group = groupRepository.findByIdWithMembers(event.groupId());
            
            if (group == null) {
                logger.warning("⚠️ Group not found for notification: " + event.groupId());
                return;
            }
            
            logger.info("📋 Group found: " + group.getName() + " with " + group.getMembers().size() + " members");
            
            // Get all group member UUIDs except the creator
            var allMembers = group.getMembers().stream()
                    .map(member -> member.userId())
                    .toList();
                    
            logger.info("👤 All member IDs: " + allMembers);
            logger.info("👤 Creator ID: " + event.createdBy());
            
            var recipientUserIds = group.getMembers().stream()
                    .map(member -> member.userId())
                    .filter(userId -> !userId.equals(event.createdBy()))
                    .map(userId -> userId.toString()) // Convert UUID to string for storage
                    .toList();
            
            logger.info("📧 Recipients (excluding creator): " + recipientUserIds);
            
            if (recipientUserIds.isEmpty()) {
                logger.info("ℹ️ No members to notify for group creation (only creator in group or creator excluded)");
                return;
            }
            
            // Create notification command
            var command = new CreateNotificationCommand(
                    "Added to new group",
                    "You have been added to the group '" + event.groupName(),
                    recipientUserIds,
                    NotificationPriority.MEDIUM
            );
            
            logger.info("📤 Creating notification command: " + command.title());
            
            // Send notification
            var result = notificationCommandService.handle(command);
            
            if (result.isPresent()) {
                logger.info("✅ Successfully created group creation notification for " + 
                           recipientUserIds.size() + " members. Notification ID: " + result.get().getId());
            } else {
                logger.severe("❌ Failed to create group creation notification");
            }
            
        } catch (Exception e) {
            logger.severe("💥 Error processing group creation notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
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
import synera.centralis.api.shared.domain.events.MessageSentInGroupEvent;

/**
 * Event handler for messages sent in group events.
 * Notifies all group members except the sender when a message is sent to a group.
 */
@Component
public class GroupMessageNotificationHandler {
    
    private static final Logger logger = Logger.getLogger(GroupMessageNotificationHandler.class.getName());
    
    private final NotificationCommandService notificationCommandService;
    private final GroupRepository groupRepository;
    
    public GroupMessageNotificationHandler(
            NotificationCommandService notificationCommandService,
            GroupRepository groupRepository) {
        this.notificationCommandService = notificationCommandService;
        this.groupRepository = groupRepository;
    }
    
    /**
     * Handles message sent in group events by creating notifications for group members except sender
     * @param event The message sent in group event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MessageSentInGroupEvent event) {
        logger.info("Processing group message notification for group: " + event.groupId() + 
                   ", message: " + event.messageId());
        
        try {
            // Find the group with members eagerly loaded
            var group = groupRepository.findByIdWithMembers(event.groupId());
            
            if (group == null) {
                logger.warning("Group not found for notification: " + event.groupId());
                return;
            }
            
            // Get all group member usernames except the sender
            var recipientUsernames = group.getMembers().stream()
                    .map(member -> member.userId())
                    .filter(userId -> !userId.equals(event.senderId()))
                    .map(userId -> userId.toString()) // Convert UUID to string for now
                    .toList();
            
            if (recipientUsernames.isEmpty()) {
                logger.info("No members to notify for group message (only sender in group or sender excluded)");
                return;
            }
            
            // Create notification command
            var command = new CreateNotificationCommand(
                    " New message in " + group.getName(),
                    truncateMessage(event.messageContent()),
                    recipientUsernames,
                    NotificationPriority.MEDIUM
            );
            
            // Send notification
            var result = notificationCommandService.handle(command);
            
            if (result.isPresent()) {
                logger.info("Successfully created group message notification for " + 
                           recipientUsernames.size() + " members");
            } else {
                logger.severe("Failed to create group message notification");
            }
            
        } catch (Exception e) {
            logger.severe("Error processing group message notification: " + e.getMessage());
        }
    }
    
    /**
     * Truncates the message content for notification preview
     * @param content The original message content
     * @return Truncated content with ellipsis if needed
     */
    private String truncateMessage(String content) {
        if (content == null) return "";
        
        final int MAX_LENGTH = 20;
        if (content.length() <= MAX_LENGTH) {
            return content;
        }
        
        return content.substring(0, MAX_LENGTH) + "...";
    }
}
package synera.centralis.api.notification.application.internal.eventhandlers;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class GroupMessageNotificationHandler {

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
        try {
            // Find the group with members eagerly loaded
            var group = groupRepository.findByIdWithMembers(event.groupId());

            if (group == null) {
                log.warn("Group not found for message notification: {}", event.groupId());
                return;
            }

            // Get all group member usernames except the sender
            var recipientUsernames = group.getMembers().stream()
                    .map(member -> member.userId())
                    .filter(userId -> !userId.equals(event.senderId()))
                    .map(userId -> userId.toString()) // Convert UUID to string for now
                    .toList();

            if (recipientUsernames.isEmpty()) {
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
                log.info("Group message notification created for {} members", recipientUsernames.size());
            } else {
                log.error("Failed to create group message notification");
            }

        } catch (Exception e) {
            log.error("Error processing group message notification", e);
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

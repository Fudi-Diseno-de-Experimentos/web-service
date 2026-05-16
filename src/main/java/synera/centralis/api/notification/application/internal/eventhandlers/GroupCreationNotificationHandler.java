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
import synera.centralis.api.shared.domain.events.GroupCreatedEvent;

/**
 * Event handler for group created events.
 * Notifies all group members except the creator when a new group is created.
 */
@Slf4j
@Component
public class GroupCreationNotificationHandler {

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
        try {
            // Find the newly created group with members eagerly loaded
            var group = groupRepository.findByIdWithMembers(event.groupId());

            if (group == null) {
                log.warn("Group not found for creation notification: {}", event.groupId());
                return;
            }

            var recipientUserIds = group.getMembers().stream()
                    .map(member -> member.userId())
                    .filter(userId -> !userId.equals(event.createdBy()))
                    .map(userId -> userId.toString()) // Convert UUID to string for storage
                    .toList();

            if (recipientUserIds.isEmpty()) {
                return;
            }

            // Create notification command
            var command = new CreateNotificationCommand(
                    "Added to new group",
                    "You have been added to the group '" + event.groupName(),
                    recipientUserIds,
                    NotificationPriority.MEDIUM
            );

            // Send notification
            var result = notificationCommandService.handle(command);

            if (result.isPresent()) {
                log.info("Group creation notification created for {} members", recipientUserIds.size());
            } else {
                log.error("Failed to create group creation notification");
            }

        } catch (Exception e) {
            log.error("Error processing group creation notification", e);
        }
    }
}

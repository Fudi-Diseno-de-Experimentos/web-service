package synera.centralis.api.notification.application.internal.eventhandlers;

import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
import synera.centralis.api.notification.domain.services.NotificationCommandService;
import synera.centralis.api.shared.domain.events.EventCreatedEvent;

@Slf4j
@Component
public class EventCreatedNotificationHandler {

    private final NotificationCommandService notificationCommandService;

    public EventCreatedNotificationHandler(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(EventCreatedEvent event) {
        log.info("📅 Processing event creation notification for: " + event.title());

        try {

            Set<UUID> recipients = event.recipientIds();
            log.info("📥 Recipients raw from event: " + (recipients == null ? "null" : recipients.toString()));

            if (recipients != null && !recipients.isEmpty()) {
                String creatorIdStr = event.createdBy().toString();

                // Convertir a strings y eliminar explícitamente al creador
                var recipientIds = recipients.stream()
                        .map(UUID::toString)
                        .filter(idStr -> !idStr.equals(creatorIdStr))
                        .toList();

                log.info("📧 RecipientIds after excluding creator: " + recipientIds);

                if (!recipientIds.isEmpty()) {
                    var attendeeCommand = new CreateNotificationCommand(
                            "Has sido añadido a un evento",
                            "Has sido añadido al evento: " + event.title(),
                            recipientIds,
                            NotificationPriority.MEDIUM
                    );
                    var result = notificationCommandService.handle(attendeeCommand);
                    if (result != null && result.isPresent()) {
                        log.info("✅ Notification sent to attendees: " + recipientIds.size());
                    } else {
                        log.error("❌ Failed to create notifications for attendees (service returned empty)");
                    }
                } else {
                    log.info("ℹ️ No attendees to notify (after excluding creator)");
                }
            } else {
                log.info("ℹ️ No recipientIds present in EventCreatedEvent");
            }

        } catch (Exception e) {
            log.error("❌ Error processing event notification", e);
        }
    }
}

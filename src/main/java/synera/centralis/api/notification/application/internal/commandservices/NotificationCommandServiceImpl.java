package synera.centralis.api.notification.application.internal.commandservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.notification.domain.model.aggregates.Notification;
import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.commands.UpdateNotificationStatusCommand;
import synera.centralis.api.notification.domain.model.events.NotificationCreatedEvent;
import synera.centralis.api.notification.domain.model.events.NotificationSentEvent;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
import synera.centralis.api.notification.domain.services.NotificationCommandService;
import synera.centralis.api.notification.infrastructure.persistence.jpa.repositories.NotificationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository, ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<Notification> handle(CreateNotificationCommand command) {
        log.info("🔔 NOTIFICATION SERVICE: Creating notification");
        log.info("📋 Title: " + command.title());
        log.info("📝 Message: " + command.message());
        log.info("👥 Recipients: " + command.recipients());
        log.info("⚡ Priority: " + command.priority());

        var notification = new Notification(
                command.title(),
                command.message(),
                command.recipients(),
                command.priority()
        );

        try {
            var savedNotification = notificationRepository.save(notification);
            log.info("✅ Notification saved with ID: " + savedNotification.getId());

            // Single publication path: explicit Spring event consumed by the FCM
            // handler. (The aggregate-root addDomainEvent path was dead — Spring
            // Data only publishes registered events during save(), and this
            // entity is not saved again after registration.)
            eventPublisher.publishEvent(new NotificationCreatedEvent(
                    savedNotification.getId(),
                    savedNotification.getTitle(),
                    savedNotification.getMessage(),
                    savedNotification.getRecipients()
            ));

            return Optional.of(savedNotification);
        } catch (Exception e) {
            log.error("Error creating notification", e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Notification> handle(UpdateNotificationStatusCommand command) {
        var notification = notificationRepository.findById(command.notificationId());

        if (notification.isEmpty()) {
            return Optional.empty();
        }

        var existingNotification = notification.get();

        // Update status using business methods
        switch (command.status()) {
            case SENT -> existingNotification.markAsSent();
            case FAILED -> existingNotification.markAsFailed();
            case READ -> existingNotification.markAsRead();
            default -> existingNotification.setStatus(command.status());
        }

        try {
            var savedNotification = notificationRepository.save(existingNotification);

            // Register domain event
            savedNotification.addDomainEvent(new NotificationSentEvent(
                    savedNotification.getId(),
                    savedNotification.getStatus()
            ));

            return Optional.of(savedNotification);
        } catch (Exception e) {
            log.error("Error updating notification status", e);
            return Optional.empty();
        }
    }

    @Override
    @Async("notificationTaskExecutor")
    @Transactional
    public CompletableFuture<List<Notification>> createBulkNotifications(
            String title,
            String message,
            List<String> recipients,
            NotificationPriority priority) {

        log.info("Creating bulk notification for " + recipients.size() + " recipients");

        try {
            var command = new CreateNotificationCommand(title, message, recipients, priority);
            var result = handle(command);

            if (result.isPresent()) {
                return CompletableFuture.completedFuture(List.of(result.get()));
            } else {
                return CompletableFuture.completedFuture(List.of());
            }

        } catch (Exception e) {
            log.error("Error in bulk notification creation", e);
            return CompletableFuture.completedFuture(List.of());
        }
    }

    @Override
    @Async("notificationTaskExecutor")
    @Transactional
    public CompletableFuture<List<Notification>> createBatchNotifications(List<CreateNotificationCommand> notifications) {
        log.info("Creating batch notifications for " + notifications.size() + " commands");

        List<Notification> createdNotifications = new ArrayList<>();

        for (var command : notifications) {
            try {
                var result = handle(command);
                result.ifPresent(createdNotifications::add);
            } catch (Exception e) {
                log.error("Error creating notification in batch", e);
            }
        }

        return CompletableFuture.completedFuture(createdNotifications);
    }
}

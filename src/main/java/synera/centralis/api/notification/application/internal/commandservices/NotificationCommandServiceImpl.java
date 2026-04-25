package synera.centralis.api.notification.application.internal.commandservices;

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
import java.util.logging.Logger;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {
    
    private static final Logger logger = Logger.getLogger(NotificationCommandServiceImpl.class.getName());
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public NotificationCommandServiceImpl(NotificationRepository notificationRepository, ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public Optional<Notification> handle(CreateNotificationCommand command) {
        logger.info("🔔 NOTIFICATION SERVICE: Creating notification");
        logger.info("📋 Title: " + command.title());
        logger.info("📝 Message: " + command.message());
        logger.info("👥 Recipients: " + command.recipients());
        logger.info("⚡ Priority: " + command.priority());
        
        var notification = new Notification(
                command.title(),
                command.message(),
                command.recipients(),
                command.priority()
        );
        
        try {
            var savedNotification = notificationRepository.save(notification);
            logger.info("✅ Notification saved with ID: " + savedNotification.getId());
            
            // Register domain event
            savedNotification.addDomainEvent(new NotificationCreatedEvent(
                    savedNotification.getId(),
                    savedNotification.getTitle(),
                    savedNotification.getMessage(),
                    savedNotification.getRecipients()
            ));
            
            // Publish Spring event for FCM processing
            eventPublisher.publishEvent(new NotificationCreatedEvent(
                    savedNotification.getId(),
                    savedNotification.getTitle(),
                    savedNotification.getMessage(),
                    savedNotification.getRecipients()
            ));
            
            return Optional.of(savedNotification);
        } catch (Exception e) {
            logger.severe("❌ Error creating notification: " + e.getMessage());
            e.printStackTrace();
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
            logger.severe("Error updating notification status: " + e.getMessage());
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
        
        logger.info("Creating bulk notification for " + recipients.size() + " recipients");
        
        try {
            var command = new CreateNotificationCommand(title, message, recipients, priority);
            var result = handle(command);
            
            if (result.isPresent()) {
                return CompletableFuture.completedFuture(List.of(result.get()));
            } else {
                return CompletableFuture.completedFuture(List.of());
            }
            
        } catch (Exception e) {
            logger.severe("Error in bulk notification creation: " + e.getMessage());
            return CompletableFuture.completedFuture(List.of());
        }
    }
    
    @Override
    @Async("notificationTaskExecutor")
    @Transactional
    public CompletableFuture<List<Notification>> createBatchNotifications(List<CreateNotificationCommand> notifications) {
        logger.info("Creating batch notifications for " + notifications.size() + " commands");
        
        List<Notification> createdNotifications = new ArrayList<>();
        
        for (var command : notifications) {
            try {
                var result = handle(command);
                result.ifPresent(createdNotifications::add);
            } catch (Exception e) {
                logger.severe("Error creating notification in batch: " + e.getMessage());
            }
        }
        
        return CompletableFuture.completedFuture(createdNotifications);
    }
}
package synera.centralis.api.notification.domain.services;

import synera.centralis.api.notification.domain.model.aggregates.Notification;
import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.commands.UpdateNotificationStatusCommand;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface NotificationCommandService {
    
    /**
     * Handle the creation of a new notification
     * @param command The command containing notification data
     * @return The created notification
     */
    Optional<Notification> handle(CreateNotificationCommand command);
    
    /**
     * Handle updating the status of a notification
     * @param command The command containing notification ID and new status
     * @return The updated notification
     */
    Optional<Notification> handle(UpdateNotificationStatusCommand command);
    
    /**
     * Create multiple notifications asynchronously for bulk operations
     * @param title The notification title
     * @param message The notification message
     * @param recipients List of recipient usernames
     * @param priority The notification priority
     * @return CompletableFuture with the list of created notifications
     */
    CompletableFuture<List<Notification>> createBulkNotifications(
            String title, 
            String message, 
            List<String> recipients, 
            NotificationPriority priority);
    
    /**
     * Create notifications for a group of users with individual messages
     * @param notifications List of notification commands to process
     * @return CompletableFuture with the list of created notifications
     */
    CompletableFuture<List<Notification>> createBatchNotifications(List<CreateNotificationCommand> notifications);
}
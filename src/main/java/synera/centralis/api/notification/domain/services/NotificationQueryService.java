package synera.centralis.api.notification.domain.services;

import synera.centralis.api.notification.domain.model.aggregates.Notification;
import synera.centralis.api.notification.domain.model.queries.GetNotificationByIdQuery;
import synera.centralis.api.notification.domain.model.queries.GetNotificationsByUserIdQuery;
import synera.centralis.api.notification.domain.model.queries.GetNotificationStatusQuery;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryService {
    
    /**
     * Handle retrieving notifications for a specific user
     * @param query The query containing user ID
     * @return List of notifications for the user
     */
    List<Notification> handle(GetNotificationsByUserIdQuery query);
    
    /**
     * Handle retrieving the status of a specific notification
     * @param query The query containing notification ID
     * @return The notification if found
     */
    Optional<Notification> handle(GetNotificationStatusQuery query);
    
    /**
     * Handle retrieving a specific notification by ID
     * @param query The query containing notification ID
     * @return The notification if found
     */
    Optional<Notification> handle(GetNotificationByIdQuery query);
}
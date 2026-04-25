package synera.centralis.api.notification.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.notification.domain.model.aggregates.Notification;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    /**
     * Find notifications by recipient ID
     * @param recipientId The recipient user ID
     * @return List of notifications for the recipient
     */
    @Query("SELECT n FROM Notification n JOIN n.recipients r WHERE r = :recipientId ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientsContaining(@Param("recipientId") String recipientId);
    
    /**
     * Find notifications by recipient ID and status
     * @param recipientId The recipient user ID
     * @param status The notification status
     * @return List of notifications matching criteria
     */
    @Query("SELECT n FROM Notification n JOIN n.recipients r WHERE r = :recipientId AND n.status = :status ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientsContainingAndStatus(@Param("recipientId") String recipientId, @Param("status") NotificationStatus status);
    
    /**
     * Find notifications by status
     * @param status The notification status
     * @return List of notifications with the specified status
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * Count unread notifications for a recipient
     * @param recipientId The recipient user ID
     * @return Count of unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n JOIN n.recipients r WHERE r = :recipientId AND n.status != 'READ'")
    Long countUnreadByRecipient(@Param("recipientId") String recipientId);
}
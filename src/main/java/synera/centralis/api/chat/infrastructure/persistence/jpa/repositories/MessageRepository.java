package synera.centralis.api.chat.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JPA Repository interface for Message entity.
 * Provides data access methods for message operations.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Find all messages in a specific group, ordered by sent date.
     * @param groupId the group ID
     * @return list of messages in the group, ordered by sent date
     */
    @Query("SELECT m FROM Message m WHERE m.groupId = :groupId ORDER BY m.sentAt ASC")
    List<Message> findByGroupIdOrderBySentAtAsc(@Param("groupId") UUID groupId);

    /**
     * Find all messages sent by a specific user, ordered by sent date.
     * @param senderId the sender user ID
     * @return list of messages sent by the user, ordered by sent date
     */
    @Query("SELECT m FROM Message m WHERE m.senderId = :senderId ORDER BY m.sentAt DESC")
    List<Message> findBySenderIdOrderBySentAtDesc(@Param("senderId") UserId senderId);

    /**
     * Find all messages with a specific status, ordered by sent date.
     * @param status the message status
     * @return list of messages with the specified status, ordered by sent date
     */
    @Query("SELECT m FROM Message m WHERE m.status = :status ORDER BY m.sentAt DESC")
    List<Message> findByStatusOrderBySentAtDesc(@Param("status") MessageStatus status);

    /**
     * Find all visible messages in a group (not deleted).
     * @param groupId the group ID
     * @return list of visible messages in the group
     */
    @Query("SELECT m FROM Message m WHERE m.groupId = :groupId AND m.status != :deletedStatus ORDER BY m.sentAt ASC")
    List<Message> findVisibleMessagesByGroupId(@Param("groupId") UUID groupId, @Param("deletedStatus") MessageStatus deletedStatus);

    /**
     * Find messages by status in a specific group.
     * @param groupId the group ID
     * @param status the message status
     * @return list of messages with the specified status
     */
    @Query("SELECT m FROM Message m WHERE m.groupId = :groupId AND m.status = :status ORDER BY m.sentAt ASC")
    List<Message> findByGroupIdAndStatus(@Param("groupId") UUID groupId, @Param("status") MessageStatus status);

    /**
     * Count messages in a group.
     * @param groupId the group ID
     * @return the number of messages in the group
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.groupId = :groupId")
    Long countByGroupId(@Param("groupId") UUID groupId);

    /**
     * Count visible messages in a group (not deleted).
     * @param groupId the group ID
     * @return the number of visible messages in the group
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.groupId = :groupId AND m.status != :deletedStatus")
    Long countVisibleMessagesByGroupId(@Param("groupId") UUID groupId, @Param("deletedStatus") MessageStatus deletedStatus);

    /**
     * Find messages sent after a specific date in a group.
     * @param groupId the group ID
     * @param afterDate the date after which to search
     * @return list of messages sent after the specified date
     */
    @Query("SELECT m FROM Message m WHERE m.groupId = :groupId AND m.sentAt > :afterDate ORDER BY m.sentAt ASC")
    List<Message> findByGroupIdAndSentAtAfter(@Param("groupId") UUID groupId, @Param("afterDate") Date afterDate);

    /**
     * Delete all messages in a group.
     * @param groupId the group ID
     */
    @Modifying
    @Query("DELETE FROM Message m WHERE m.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") UUID groupId);
}
package synera.centralis.api.event.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository interface for Event aggregate.
 * Provides data access methods for event operations.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Find all events where the specified user is a recipient.
     * @param recipientId the user ID to search for
     * @return list of events where the user is a recipient
     */
    @Query("SELECT e FROM Event e JOIN e.recipients r WHERE r = :recipientId")
    List<Event> findByRecipientsContaining(@Param("recipientId") UserId recipientId);

    /**
     * Find all events created by a specific user.
     * @param createdBy the creator user ID
     * @return list of events created by the user
     */
    @Query("SELECT e FROM Event e WHERE e.createdBy = :createdBy")
    List<Event> findByCreatedBy(@Param("createdBy") UserId createdBy);

    /**
     * Check if an event exists by ID.
     * @param eventId the event ID
     * @return true if event exists, false otherwise
     */
    boolean existsById(UUID eventId);

    /**
     * Find events by title containing the specified text (case-insensitive).
     * @param title the text to search for in event titles
     * @return list of events with matching titles
     */
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Event> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Count events by creator.
     * @param createdBy the creator user ID
     * @return number of events created by the user
     */
    @Query("SELECT COUNT(e) FROM Event e WHERE e.createdBy = :createdBy")
    long countByCreatedBy(@Param("createdBy") UserId createdBy);

    /**
     * Count events where user is a recipient.
     * @param recipientId the recipient user ID
     * @return number of events where the user is a recipient
     */
    @Query("SELECT COUNT(e) FROM Event e JOIN e.recipients r WHERE r = :recipientId")
    long countByRecipientsContaining(@Param("recipientId") UserId recipientId);
}


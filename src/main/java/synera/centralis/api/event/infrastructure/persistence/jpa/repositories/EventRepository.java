package synera.centralis.api.event.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import synera.centralis.api.event.domain.model.valueobjects.SpaceId;
import java.util.Optional;

/**
 * JPA Repository interface for Event aggregate.
 * Provides data access methods for event operations.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findAllByCompanyId(CompanyId companyId);

    Optional<Event> findByIdAndCompanyId(UUID id, CompanyId companyId);

    /**
     * Find all events where the specified user is a recipient.
     * @param recipientId the user ID to search for
     * @return list of events where the user is a recipient
     */
    @Query("SELECT e FROM Event e JOIN e.recipients r WHERE r = :recipientId AND e.companyId = :companyId")
    List<Event> findByRecipientsContainingAndCompanyId(@Param("recipientId") UserId recipientId, @Param("companyId") CompanyId companyId);

    @Query("SELECT e FROM Event e JOIN e.recipients r WHERE r = :recipientId")
    List<Event> findByRecipientsContaining(@Param("recipientId") UserId recipientId);

    /**
     * Find all events created by a specific user.
     * @param createdBy the creator user ID
     * @return list of events created by the user
     */
    @Query("SELECT e FROM Event e WHERE e.createdBy = :createdBy AND e.companyId = :companyId")
    List<Event> findByCreatedByAndCompanyId(@Param("createdBy") UserId createdBy, @Param("companyId") CompanyId companyId);

    @Query("SELECT e FROM Event e WHERE e.createdBy = :createdBy")
    List<Event> findByCreatedBy(@Param("createdBy") UserId createdBy);

    /**
     * Check if an event exists by ID.
     * @param eventId the event ID
     * @return true if event exists, false otherwise
     */
    boolean existsById(UUID eventId);
    boolean existsByIdAndCompanyId(UUID eventId, CompanyId companyId);

    /**
     * Find events by title containing the specified text (case-insensitive).
     * @param title the text to search for in event titles
     * @return list of events with matching titles
     */
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')) AND e.companyId = :companyId")
    List<Event> findByTitleContainingIgnoreCaseAndCompanyId(@Param("title") String title, @Param("companyId") CompanyId companyId);

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

    /**
     * Day-level booking conflict check: is there already an event in this company
     * that books the given space on the same calendar day? The {@code [start, end)}
     * window is the target day; {@code excludeId} lets an update skip itself (pass
     * null on create). Clock time is intentionally ignored — only the day matters.
     */
    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.companyId = :companyId AND e.spaceId = :spaceId " +
            "AND e.date >= :start AND e.date < :end AND (:excludeId IS NULL OR e.id <> :excludeId)")
    boolean existsBookingConflict(@Param("companyId") CompanyId companyId,
                                  @Param("spaceId") SpaceId spaceId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("excludeId") UUID excludeId);

    /**
     * Space IDs booked within this company on a given calendar day ({@code [start, end)}).
     * Used to compute per-day room availability.
     */
    @Query("SELECT e.spaceId.spaceId FROM Event e WHERE e.companyId = :companyId AND e.spaceId IS NOT NULL " +
            "AND e.date >= :start AND e.date < :end")
    List<UUID> findBookedSpaceIds(@Param("companyId") CompanyId companyId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    /**
     * Does the given space have any booking at or after {@code now}? Guards space deletion.
     */
    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.companyId = :companyId AND e.spaceId = :spaceId " +
            "AND e.date >= :now")
    boolean existsFutureBooking(@Param("companyId") CompanyId companyId,
                                @Param("spaceId") SpaceId spaceId,
                                @Param("now") LocalDateTime now);
}


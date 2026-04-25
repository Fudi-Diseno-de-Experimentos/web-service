package synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;

import java.util.List;
import java.util.UUID;

/**
 * Announcement Repository
 * JPA repository interface for Announcement aggregate
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    /**
     * Find announcements by priority level
     */
    @Query("SELECT a FROM Announcement a WHERE a.priority.level = :priorityLevel ORDER BY a.createdAt DESC")
    List<Announcement> findByPriorityLevel(@Param("priorityLevel") Priority.PriorityLevel priorityLevel);

    /**
     * Find announcements by creator
     */
    List<Announcement> findByCreatedByOrderByCreatedAtDesc(UUID createdBy);

    /**
     * Find all announcements ordered by creation date
     */
    List<Announcement> findAllByOrderByCreatedAtDesc();

    /**
     * Count announcements by creator
     */
    long countByCreatedBy(UUID createdBy);

    /**
     * Count announcements by priority level
     */
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.priority.level = :priorityLevel")
    long countByPriorityLevel(@Param("priorityLevel") Priority.PriorityLevel priorityLevel);
}
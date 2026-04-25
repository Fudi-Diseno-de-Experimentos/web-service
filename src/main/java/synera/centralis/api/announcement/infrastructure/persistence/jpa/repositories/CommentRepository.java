package synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synera.centralis.api.announcement.domain.model.entities.Comment;

import java.util.List;
import java.util.UUID;

/**
 * Comment Repository
 * JPA repository interface for Comment entity
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * Find comments by announcement ID ordered by creation date
     */
    List<Comment> findByAnnouncementIdOrderByCreatedAtAsc(UUID announcementId);

    /**
     * Find comments by employee ID ordered by creation date
     */
    List<Comment> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    /**
     * Count comments by announcement ID
     */
    long countByAnnouncementId(UUID announcementId);

    /**
     * Count comments by employee ID
     */
    long countByEmployeeId(UUID employeeId);

    /**
     * Delete all comments by announcement ID
     */
    void deleteByAnnouncementId(UUID announcementId);
}
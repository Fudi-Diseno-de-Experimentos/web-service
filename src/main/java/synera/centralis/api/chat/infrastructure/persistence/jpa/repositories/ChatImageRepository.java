package synera.centralis.api.chat.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.chat.domain.model.entities.ChatImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ChatImage persistence operations.
 */
@Repository
public interface ChatImageRepository extends JpaRepository<ChatImage, UUID> {

    /**
     * Find all chat images by group ID, ordered by sent date descending.
     */
    @Query("SELECT ci FROM ChatImage ci WHERE ci.groupId = :groupId ORDER BY ci.sentAt DESC")
    List<ChatImage> findByGroupIdOrderBySentAtDesc(@Param("groupId") UUID groupId);

    /**
     * Find all visible chat images by group ID, ordered by sent date descending.
     */
    @Query("SELECT ci FROM ChatImage ci WHERE ci.groupId = :groupId AND ci.isVisible = true ORDER BY ci.sentAt DESC")
    List<ChatImage> findVisibleByGroupIdOrderBySentAtDesc(@Param("groupId") UUID groupId);

    /**
     * Find a chat image by ID only if it's visible.
     */
    @Query("SELECT ci FROM ChatImage ci WHERE ci.imageId = :imageId AND ci.isVisible = true")
    Optional<ChatImage> findVisibleByImageId(@Param("imageId") UUID imageId);

    /**
     * Count visible chat images in a group.
     */
    @Query("SELECT COUNT(ci) FROM ChatImage ci WHERE ci.groupId = :groupId AND ci.isVisible = true")
    long countVisibleByGroupId(@Param("groupId") UUID groupId);
}
package synera.centralis.api.dashboard.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.valueobjects.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ContentView aggregate
 */
@Repository
public interface ContentViewRepository extends JpaRepository<ContentView, UUID> {

    /**
     * Find existing view by user and content (for duplicate prevention)
     * @param userId User ID
     * @param contentId Content ID (announcement or event)
     * @param contentType Content type
     * @return Optional ContentView if exists
     */
    Optional<ContentView> findByUserIdAndContentIdAndContentType(
            UserId userId, 
            UUID contentId, 
            ContentType contentType
    );

    /**
     * Find all announcements viewed by a user
     * @param userId User ID
     * @param contentType Should be ANNOUNCEMENT
     * @return List of ContentViews for announcements
     */
    List<ContentView> findByUserIdAndContentTypeOrderByViewDateTimeDesc(
            UserId userId, 
            ContentType contentType
    );

    /**
     * Find all users who viewed a specific announcement
     * @param contentId Announcement ID
     * @param contentType Should be ANNOUNCEMENT  
     * @return List of ContentViews for the announcement
     */
    List<ContentView> findByContentIdAndContentTypeOrderByViewDateTimeDesc(
            UUID contentId, 
            ContentType contentType
    );

    /**
     * Count total announcement views
     * @return Total number of announcement views
     */
    @Query("SELECT COUNT(cv) FROM ContentView cv WHERE cv.contentType = :contentType")
    long countByContentType(@Param("contentType") ContentType contentType);

    /**
     * Count unique users who viewed announcements
     * @return Number of unique users with announcement views
     */
    @Query("SELECT COUNT(DISTINCT cv.userId) FROM ContentView cv WHERE cv.contentType = :contentType")
    long countDistinctUsersByContentType(@Param("contentType") ContentType contentType);

    /**
     * Find most viewed content by type
     * @param contentType Content type (ANNOUNCEMENT or EVENT)
     * @return List of content IDs ordered by view count descending
     */
    @Query("SELECT cv.contentId, COUNT(cv) as viewCount FROM ContentView cv " +
           "WHERE cv.contentType = :contentType " +
           "GROUP BY cv.contentId " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedContentByType(@Param("contentType") ContentType contentType);

    /**
     * Find top active users across all content types
     * @return List of user IDs with their total view counts
     */
    @Query("SELECT cv.userId, COUNT(cv) as totalViews FROM ContentView cv " +
           "GROUP BY cv.userId " +
           "ORDER BY totalViews DESC")
    List<Object[]> findTopActiveUsers();

    /**
     * Count views for specific content
     * @param contentId Content ID
     * @param contentType Content type
     * @return Number of views for the content
     */
    long countByContentIdAndContentType(UUID contentId, ContentType contentType);

    /**
     * Find all views for dashboard summary
     * @return All content views ordered by view date time desc
     */
    List<ContentView> findAllByOrderByViewDateTimeDesc();
}
package synera.centralis.api.chat.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository interface for Group aggregate.
 * Provides data access methods for group operations.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    /**
     * Find all groups where the specified user is a member.
     * @param memberId the user ID to search for
     * @return list of groups where the user is a member
     */
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m = :memberId")
    List<Group> findByMembersContaining(@Param("memberId") UserId memberId);

    /**
     * Check if a group exists by ID.
     * @param groupId the group ID
     * @return true if group exists, false otherwise
     */
    boolean existsById(UUID groupId);

    /**
     * Find groups by name containing the specified text (case-insensitive).
     * @param name the text to search for in group names
     * @return list of groups with matching names
     */
    @Query("SELECT g FROM Group g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Group> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find groups by visibility.
     * @param visibility the group visibility
     * @return list of groups with the specified visibility
     */
    @Query("SELECT g FROM Group g WHERE g.visibility = :visibility")
    List<Group> findByVisibility(@Param("visibility") String visibility);

    /**
     * Find a group by ID with members eagerly loaded.
     * Used for notification processing to avoid LazyInitializationException.
     * @param groupId the group ID
     * @return the group with members loaded, or null if not found
     */
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.members WHERE g.id = :groupId")
    Group findByIdWithMembers(@Param("groupId") UUID groupId);

    /**
     * Count the number of members in a group.
     * @param groupId the group ID
     * @return the number of members in the group
     */
    @Query("SELECT SIZE(g.members) FROM Group g WHERE g.id = :groupId")
    Integer countMembersByGroupId(@Param("groupId") UUID groupId);
}
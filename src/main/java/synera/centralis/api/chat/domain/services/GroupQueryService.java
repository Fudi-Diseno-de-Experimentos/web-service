package synera.centralis.api.chat.domain.services;

import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for handling group-related queries.
 * Defines the contract for all group query operations.
 */
public interface GroupQueryService {

    /**
     * Handles retrieving a group by its ID.
     * @param query the get group by ID query
     * @return the group if found, empty otherwise
     */
    Optional<Group> handle(GetGroupByIdQuery query);

    /**
     * Handles retrieving all groups where a user is a member.
     * @param query the get groups by member ID query
     * @return list of groups where the user is a member
     */
    List<Group> handle(GetGroupsByMemberIdQuery query);

    /**
     * Handles retrieving all groups in the system.
     * @param query the get all groups query
     * @return list of all groups
     */
    List<Group> handle(GetAllGroupsQuery query);
}
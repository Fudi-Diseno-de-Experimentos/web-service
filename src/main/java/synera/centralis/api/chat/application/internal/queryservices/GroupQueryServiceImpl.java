package synera.centralis.api.chat.application.internal.queryservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.queries.*;
import synera.centralis.api.chat.domain.services.GroupQueryService;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.GroupRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of GroupQueryService.
 * Handles all group-related query operations.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class GroupQueryServiceImpl implements GroupQueryService {

    private final GroupRepository groupRepository;

    public GroupQueryServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Optional<Group> handle(GetGroupByIdQuery query) {
        try {
            log.debug("Retrieving group with ID: {}", query.groupId());
            return groupRepository.findById(query.groupId());
        } catch (Exception e) {
            log.error("Error retrieving group by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Group> handle(GetGroupsByMemberIdQuery query) {
        try {
            log.debug("Retrieving groups for member: {}", query.memberId().userId());
            return groupRepository.findByMembersContaining(query.memberId());
        } catch (Exception e) {
            log.error("Error retrieving groups by member ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Group> handle(GetAllGroupsQuery query) {
        try {
            log.debug("Retrieving all groups");
            return groupRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all groups: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
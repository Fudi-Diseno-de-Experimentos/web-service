package synera.centralis.api.chat.application.internal.commandservices;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.AddMemberToGroupCommand;
import synera.centralis.api.chat.domain.model.commands.CreateGroupCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteGroupCommand;
import synera.centralis.api.chat.domain.model.commands.RemoveMemberFromGroupCommand;
import synera.centralis.api.chat.domain.model.commands.UpdateGroupCommand;
import synera.centralis.api.chat.domain.model.commands.UpdateGroupVisibilityCommand;
import synera.centralis.api.chat.domain.services.GroupCommandService;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.GroupRepository;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.MessageRepository;
import synera.centralis.api.shared.domain.events.GroupCreatedEvent;

/**
 * Implementation of GroupCommandService.
 * Handles all group-related command operations with business logic and validation.
 */
@Slf4j
@Service
public class GroupCommandServiceImpl implements GroupCommandService {

    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GroupCommandServiceImpl(GroupRepository groupRepository, 
                                 MessageRepository messageRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.groupRepository = groupRepository;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<Group> handle(CreateGroupCommand command) {
        try {
            log.info("Creating new group with name: {}", command.name());
            
            var group = new Group(
                    command.name(),
                    command.description(),
                    command.imageUrl(),
                    command.visibility(),
                    command.memberIds(),
                    command.createdBy()
            );

            var savedGroup = groupRepository.save(group);
            
            log.info("=== GROUP CREATED ===");
            log.info("Group Name: {}", savedGroup.getName());
            log.info("Group ID: {}", savedGroup.getId());
            log.info("Created By: {}", savedGroup.getCreatedBy().userId());
            log.info("Members count: {}", savedGroup.getMembers().size());
            
            // Publish group created event for notifications
            log.info("🚀 PUBLISHING GROUP CREATED EVENT for group: {}", savedGroup.getId());
            
            // Extract member IDs from the group
            var memberIds = savedGroup.getMembers().stream()
                    .map(member -> member.userId())
                    .collect(java.util.stream.Collectors.toSet());
            
            log.info("👥 Member IDs: {}", memberIds);
            
            var event = GroupCreatedEvent.create(
                savedGroup.getId(),
                savedGroup.getName(),
                savedGroup.getCreatedBy().userId(),
                memberIds
            );
            
            log.info("📋 Event created: {}", event.toString());
            eventPublisher.publishEvent(event);
            log.info("✅ Group creation event published successfully");
            
            log.info("Successfully created group with ID: {}", savedGroup.getId());
            
            return Optional.of(savedGroup);
            
        } catch (Exception e) {
            log.error("Error creating group: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Group> handle(UpdateGroupCommand command) {
        try {
            log.info("Updating group with ID: {}", command.groupId());
            
            var groupOptional = groupRepository.findById(command.groupId());
            if (groupOptional.isEmpty()) {
                log.warn("Group not found with ID: {}", command.groupId());
                return Optional.empty();
            }

            var group = groupOptional.get();
            group.updateGroup(command.name(), command.description(), command.imageUrl());
            
            var savedGroup = groupRepository.save(group);
            log.info("Successfully updated group with ID: {}", savedGroup.getId());
            
            return Optional.of(savedGroup);
            
        } catch (Exception e) {
            log.error("Error updating group: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Group> handle(UpdateGroupVisibilityCommand command) {
        try {
            log.info("Updating visibility for group with ID: {}", command.groupId());
            
            var groupOptional = groupRepository.findById(command.groupId());
            if (groupOptional.isEmpty()) {
                log.warn("Group not found with ID: {}", command.groupId());
                return Optional.empty();
            }

            var group = groupOptional.get();
            group.updateVisibility(command.visibility());
            
            var savedGroup = groupRepository.save(group);
            log.info("Successfully updated visibility for group with ID: {}", savedGroup.getId());
            
            return Optional.of(savedGroup);
            
        } catch (Exception e) {
            log.error("Error updating group visibility: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Group> handle(AddMemberToGroupCommand command) {
        try {
            log.info("Adding member {} to group {}", command.memberToAdd().userId(), command.groupId());
            
            var groupOptional = groupRepository.findById(command.groupId());
            if (groupOptional.isEmpty()) {
                log.warn("Group not found with ID: {}", command.groupId());
                return Optional.empty();
            }

            var group = groupOptional.get();
            group.addMember(command.memberToAdd());
            
            var savedGroup = groupRepository.save(group);
            log.info("Successfully added member to group with ID: {}", savedGroup.getId());
            
            return Optional.of(savedGroup);
            
        } catch (Exception e) {
            log.error("Error adding member to group: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Group> handle(RemoveMemberFromGroupCommand command) {
        try {
            log.info("Removing member {} from group {}", command.memberToRemove().userId(), command.groupId());
            
            var groupOptional = groupRepository.findById(command.groupId());
            if (groupOptional.isEmpty()) {
                log.warn("Group not found with ID: {}", command.groupId());
                return Optional.empty();
            }

            var group = groupOptional.get();
            group.removeMember(command.memberToRemove());
            
            var savedGroup = groupRepository.save(group);
            log.info("Successfully removed member from group with ID: {}", savedGroup.getId());
            
            return Optional.of(savedGroup);
            
        } catch (Exception e) {
            log.error("Error removing member from group: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<UUID> handle(DeleteGroupCommand command) {
        try {
            log.info("Deleting group with ID: {}", command.groupId());
            
            if (!groupRepository.existsById(command.groupId())) {
                log.warn("Group not found with ID: {}", command.groupId());
                return Optional.empty();
            }

            // Delete all messages in the group first
            messageRepository.deleteByGroupId(command.groupId());
            
            // Delete the group
            groupRepository.deleteById(command.groupId());
            
            log.info("Successfully deleted group with ID: {}", command.groupId());
            return Optional.of(command.groupId());
            
        } catch (Exception e) {
            log.error("Error deleting group: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
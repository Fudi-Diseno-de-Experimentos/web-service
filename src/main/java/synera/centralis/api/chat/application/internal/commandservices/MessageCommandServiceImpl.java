package synera.centralis.api.chat.application.internal.commandservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.*;
import synera.centralis.api.chat.domain.model.entities.Message;

import synera.centralis.api.chat.domain.services.MessageCommandService;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.GroupRepository;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.MessageRepository;
import synera.centralis.api.shared.domain.events.MessageSentInGroupEvent;
import synera.centralis.api.shared.infrastructure.services.SseService;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

import java.time.Instant;
import java.util.Map;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of MessageCommandService.
 * Handles all message-related command operations with business logic and validation.
 */
@Slf4j
@Service
public class MessageCommandServiceImpl implements MessageCommandService {

    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SseService sseService;
    private final IamContextFacade iamContextFacade;

    public MessageCommandServiceImpl(MessageRepository messageRepository, 
                                   GroupRepository groupRepository,
                                   ApplicationEventPublisher eventPublisher,
                                   SseService sseService,
                                   IamContextFacade iamContextFacade) {
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
        this.eventPublisher = eventPublisher;
        this.sseService = sseService;
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    @Transactional
    public Optional<Message> handle(CreateMessageCommand command) {
        try {
            log.info("Creating new message in group: {}", command.groupId());
            
            // Verify group exists
            if (!groupRepository.existsById(command.groupId())) {
                log.warn("Group not found with ID: {}", command.groupId());
                return Optional.empty();
            }

            // Verify sender is a member of the group
            var groupOptional = groupRepository.findById(command.groupId());
            if (groupOptional.isPresent()) {
                var group = groupOptional.get();
                if (!group.isMember(command.senderId())) {
                    log.warn("User {} is not a member of group {}", command.senderId().userId(), command.groupId());
                    return Optional.empty();
                }
            }

            var message = new Message(command.groupId(), command.senderId(), command.body());
            var savedMessage = messageRepository.save(message);
            
            // Publish message sent event for notifications
            log.info("Publishing message sent event for group: {}", command.groupId());
            
            // Get group name for the event
            String groupName = groupOptional.map(Group::getName).orElse("Unknown Group");
            
            var event = MessageSentInGroupEvent.create(
                savedMessage.getMessageId(),
                savedMessage.getGroupId(),
                groupName,
                savedMessage.getSenderId().userId(),
                savedMessage.getBody()
            );
            
            eventPublisher.publishEvent(event);
            
            // Send real-time message via SSE
            try {
                String senderUsername = iamContextFacade.fetchUsernameByUserId(savedMessage.getSenderId().userId());
                if (senderUsername == null || senderUsername.isEmpty()) {
                    senderUsername = "Unknown";
                }
                
                Map<String, Object> sseEventData = Map.of(
                        "messageId", savedMessage.getMessageId().toString(),
                        "senderId", savedMessage.getSenderId().userId().toString(),
                        "senderUsername", senderUsername,
                        "body", savedMessage.getBody(),
                        "groupId", savedMessage.getGroupId().toString(),
                        "sentAt", Instant.now().toString(),
                        "type", "message_created"
                );
                
                sseService.sendToGroup(savedMessage.getGroupId().toString(), "message_created", sseEventData);
                
                log.info("📡 SSE: Message broadcasted to group {} - MessageID: {}", 
                        savedMessage.getGroupId(), savedMessage.getMessageId());
                
            } catch (Exception e) {
                log.warn("⚠️ SSE: Failed to broadcast message via SSE: {}", e.getMessage());
                // Continue execution even if SSE broadcast fails
            }
            
            log.info("Successfully created message with ID: {}", savedMessage.getMessageId());
            return Optional.of(savedMessage);
            
        } catch (Exception e) {
            log.error("Error creating message: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Message> handle(UpdateMessageBodyCommand command) {
        try {
            log.info("Updating message body for ID: {}", command.messageId());
            
            var messageOptional = messageRepository.findById(command.messageId());
            if (messageOptional.isEmpty()) {
                log.warn("Message not found with ID: {}", command.messageId());
                return Optional.empty();
            }

            var message = messageOptional.get();
            
            if (!message.canBeEdited()) {
                log.warn("Message with ID {} cannot be edited (status: {})", command.messageId(), message.getStatus());
                return Optional.empty();
            }

            message.updateBody(command.newBody());
            var savedMessage = messageRepository.save(message);
            
            log.info("Successfully updated message body for ID: {}", savedMessage.getMessageId());
            return Optional.of(savedMessage);
            
        } catch (Exception e) {
            log.error("Error updating message body: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Message> handle(UpdateMessageStatusCommand command) {
        try {
            log.info("Updating message status for ID: {}", command.messageId());
            
            var messageOptional = messageRepository.findById(command.messageId());
            if (messageOptional.isEmpty()) {
                log.warn("Message not found with ID: {}", command.messageId());
                return Optional.empty();
            }

            var message = messageOptional.get();
            message.updateStatus(command.newStatus());
            
            var savedMessage = messageRepository.save(message);
            log.info("Successfully updated message status for ID: {}", savedMessage.getMessageId());
            
            return Optional.of(savedMessage);
            
        } catch (Exception e) {
            log.error("Error updating message status: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<UUID> handle(DeleteMessageCommand command) {
        try {
            log.info("Deleting message with ID: {}", command.messageId());
            
            var messageOptional = messageRepository.findById(command.messageId());
            if (messageOptional.isEmpty()) {
                log.warn("Message not found with ID: {}", command.messageId());
                return Optional.empty();
            }

            var message = messageOptional.get();
            message.markAsDeleted();
            
            messageRepository.save(message);
            log.info("Successfully marked message as deleted with ID: {}", command.messageId());
            
            return Optional.of(command.messageId());
            
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
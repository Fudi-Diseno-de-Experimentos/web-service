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
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

import java.time.Instant;
import java.util.Map;

import java.util.Optional;
import java.util.UUID;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;

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
    private final IamContextFacade iamContextFacade;

    public MessageCommandServiceImpl(MessageRepository messageRepository, 
                                   GroupRepository groupRepository,
                                   ApplicationEventPublisher eventPublisher,
                                   IamContextFacade iamContextFacade) {
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
        this.eventPublisher = eventPublisher;
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    @Transactional
    public Message handle(CreateMessageCommand command) {
        try {
            log.info("Creating new message in group: {}", command.groupId());
            
            // Verify group exists
            if (!groupRepository.existsById(command.groupId())) {
                log.warn("Group not found with ID: {}", command.groupId());
                throw new ResourceNotFoundException("Group not found with ID: " + command.groupId());
            }

            // Verify sender is a member of the group
            var groupOptional = groupRepository.findById(command.groupId());
            if (groupOptional.isPresent()) {
                var group = groupOptional.get();
                if (!group.isMember(command.senderId())) {
                    log.warn("User {} is not a member of group {}", command.senderId().userId(), command.groupId());
                    throw new UnauthorizedException("User is not a member of group");
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
            
            log.info("Successfully created message with ID: {}", savedMessage.getMessageId());
            return savedMessage;
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating message: {}", e.getMessage(), e);
            throw new ValidationException("Error creating message: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Message handle(UpdateMessageBodyCommand command) {
        log.info("Updating message body for ID: {}", command.messageId());
        
        var message = messageRepository.findById(command.messageId())
                .orElseThrow(() -> {
                    log.warn("Message not found with ID: {}", command.messageId());
                    return new ResourceNotFoundException("Message not found with ID: " + command.messageId());
                });

        try {
            if (!message.canBeEdited()) {
                log.warn("Message with ID {} cannot be edited (status: {})", command.messageId(), message.getStatus());
                throw new ValidationException("Message cannot be edited");
            }

            message.updateBody(command.newBody());
            var savedMessage = messageRepository.save(message);
            
            log.info("Successfully updated message body for ID: {}", savedMessage.getMessageId());
            return savedMessage;
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating message body: {}", e.getMessage(), e);
            throw new ValidationException("Error updating message body: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Message handle(UpdateMessageStatusCommand command) {
        log.info("Updating message status for ID: {}", command.messageId());
        
        var message = messageRepository.findById(command.messageId())
                .orElseThrow(() -> {
                    log.warn("Message not found with ID: {}", command.messageId());
                    return new ResourceNotFoundException("Message not found with ID: " + command.messageId());
                });

        try {
            message.updateStatus(command.newStatus());
            
            var savedMessage = messageRepository.save(message);
            log.info("Successfully updated message status for ID: {}", savedMessage.getMessageId());
            
            return savedMessage;
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating message status: {}", e.getMessage(), e);
            throw new ValidationException("Error updating message status: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean handle(DeleteMessageCommand command) {
        log.info("Deleting message with ID: {}", command.messageId());
        
        var message = messageRepository.findById(command.messageId())
                .orElseThrow(() -> {
                    log.warn("Message not found with ID: {}", command.messageId());
                    return new ResourceNotFoundException("Message not found with ID: " + command.messageId());
                });

        try {
            message.markAsDeleted();
            
            messageRepository.save(message);
            log.info("Successfully marked message as deleted with ID: {}", command.messageId());
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage(), e);
            throw new ValidationException("Error deleting message: " + e.getMessage());
        }
    }
}
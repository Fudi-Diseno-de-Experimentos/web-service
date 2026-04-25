package synera.centralis.api.chat.application.internal.queryservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.domain.model.queries.*;
import synera.centralis.api.chat.domain.services.MessageQueryService;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.MessageRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of MessageQueryService.
 * Handles all message-related query operations.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class MessageQueryServiceImpl implements MessageQueryService {

    private final MessageRepository messageRepository;

    public MessageQueryServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Optional<Message> handle(GetMessageByIdQuery query) {
        try {
            log.debug("Retrieving message with ID: {}", query.messageId());
            return messageRepository.findById(query.messageId());
        } catch (Exception e) {
            log.error("Error retrieving message by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Message> handle(GetMessagesByGroupIdQuery query) {
        try {
            log.debug("Retrieving messages for group: {}", query.groupId());
            return messageRepository.findByGroupIdOrderBySentAtAsc(query.groupId());
        } catch (Exception e) {
            log.error("Error retrieving messages by group ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Message> handle(GetMessagesBySenderIdQuery query) {
        try {
            log.debug("Retrieving messages for sender: {}", query.senderId().userId());
            return messageRepository.findBySenderIdOrderBySentAtDesc(query.senderId());
        } catch (Exception e) {
            log.error("Error retrieving messages by sender ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Message> handle(GetMessagesByStatusQuery query) {
        try {
            log.debug("Retrieving messages with status: {}", query.status());
            return messageRepository.findByStatusOrderBySentAtDesc(query.status());
        } catch (Exception e) {
            log.error("Error retrieving messages by status: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
package synera.centralis.api.chat.domain.services;

import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for handling message-related queries.
 * Defines the contract for all message query operations.
 */
public interface MessageQueryService {

    /**
     * Handles retrieving a message by its ID.
     * @param query the get message by ID query
     * @return the message if found, empty otherwise
     */
    Optional<Message> handle(GetMessageByIdQuery query);

    /**
     * Handles retrieving all messages from a specific group.
     * @param query the get messages by group ID query
     * @return list of messages in the group
     */
    List<Message> handle(GetMessagesByGroupIdQuery query);

    /**
     * Handles retrieving all messages sent by a specific user.
     * @param query the get messages by sender ID query
     * @return list of messages sent by the user
     */
    List<Message> handle(GetMessagesBySenderIdQuery query);

    /**
     * Handles retrieving all messages with a specific status.
     * @param query the get messages by status query
     * @return list of messages with the specified status
     */
    List<Message> handle(GetMessagesByStatusQuery query);
}
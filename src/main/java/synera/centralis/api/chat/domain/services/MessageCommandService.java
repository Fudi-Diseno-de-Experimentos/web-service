package synera.centralis.api.chat.domain.services;

import synera.centralis.api.chat.domain.model.commands.*;
import synera.centralis.api.chat.domain.model.entities.Message;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain service interface for handling message-related commands.
 * Defines the contract for all message command operations.
 */
public interface MessageCommandService {

    /**
     * Handles the creation of a new message.
     * @param command the create message command
     * @return the created message
     */
    Message handle(CreateMessageCommand command);

    /**
     * Handles updating message body content.
     * @param command the update message body command
     * @return the updated message
     */
    Message handle(UpdateMessageBodyCommand command);

    /**
     * Handles updating message status.
     * @param command the update message status command
     * @return the updated message
     */
    Message handle(UpdateMessageStatusCommand command);

    /**
     * Handles deleting a message.
     * @param command the delete message command
     */
    boolean handle(DeleteMessageCommand command);
}
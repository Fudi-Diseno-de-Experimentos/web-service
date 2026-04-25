package synera.centralis.api.chat.domain.model.queries;

import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;

/**
 * Query to get messages filtered by status.
 * Retrieves all messages that have the specified status.
 */
public record GetMessagesByStatusQuery(
        MessageStatus status
) {
}
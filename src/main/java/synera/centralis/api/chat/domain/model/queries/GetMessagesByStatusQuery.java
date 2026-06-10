package synera.centralis.api.chat.domain.model.queries;

import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;

import java.util.UUID;

/**
 * Query to get messages filtered by status within a specific group.
 * Scoping by group keeps the query bounded and prevents reading another
 * group's messages.
 */
public record GetMessagesByStatusQuery(
        UUID groupId,
        MessageStatus status
) {
}
package synera.centralis.api.event.domain.model.queries;

import synera.centralis.api.event.domain.model.valueobjects.UserId;

/**
 * Query to get all events created by a specific user (manager).
 */
public record GetEventsByCreatorIdQuery(UserId creatorId) {
    public GetEventsByCreatorIdQuery {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
    }
}


package synera.centralis.api.dashboard.domain.model.queries;

import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;

/**
 * Query to get all events viewed by a specific user
 */
public record GetUserEventViewsQuery(UserId userId) {
    
    public GetUserEventViewsQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
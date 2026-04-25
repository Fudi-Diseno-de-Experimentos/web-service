package synera.centralis.api.dashboard.domain.model.queries;

import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;

/**
 * Query to get all users who viewed a specific event
 */
public record GetEventViewersQuery(EventId eventId) {
    
    public GetEventViewersQuery {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
}
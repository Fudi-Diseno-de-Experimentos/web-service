package synera.centralis.api.dashboard.domain.model.queries;

import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;

/**
 * Query to get statistics for pie chart visualization of an event
 */
public record GetEventStatsQuery(EventId eventId) {
    
    public GetEventStatsQuery {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
}
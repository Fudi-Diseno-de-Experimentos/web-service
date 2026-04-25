package synera.centralis.api.event.domain.model.queries;

import java.util.UUID;

/**
 * Query to get an event by its ID.
 */
public record GetEventByIdQuery(UUID eventId) {
    public GetEventByIdQuery {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
}

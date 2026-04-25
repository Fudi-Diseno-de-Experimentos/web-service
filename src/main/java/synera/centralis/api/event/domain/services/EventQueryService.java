package synera.centralis.api.event.domain.services;

import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for handling event-related queries.
 * Defines the contract for all event query operations.
 */
public interface EventQueryService {

    /**
     * Handles retrieving an event by its ID.
     * @param query the get event by ID query
     * @return the event or empty if not found
     */
    Optional<Event> handle(GetEventByIdQuery query);

    /**
     * Handles retrieving all events for a specific recipient.
     * @param query the get events by recipient query
     * @return list of events where the user is a recipient
     */
    List<Event> handle(GetEventsByRecipientIdQuery query);

    /**
     * Handles retrieving all events created by a specific user.
     * @param query the get events by creator query
     * @return list of events created by the user
     */
    List<Event> handle(GetEventsByCreatorIdQuery query);

    /**
     * Handles retrieving all events.
     * @param query the get all events query
     * @return list of all events
     */
    List<Event> handle(GetAllEventsQuery query);
}


package synera.centralis.api.event.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.queries.GetAllEventsQuery;
import synera.centralis.api.event.domain.model.queries.GetEventByIdQuery;
import synera.centralis.api.event.domain.model.queries.GetEventsByCreatorIdQuery;
import synera.centralis.api.event.domain.model.queries.GetEventsByRecipientIdQuery;
import synera.centralis.api.event.domain.services.EventQueryService;
import synera.centralis.api.event.infrastructure.persistence.jpa.repositories.EventRepository;

/**
 * Implementation of EventQueryService.
 * Handles all event-related query operations.
 */
@Slf4j
@Service
public class EventQueryServiceImpl implements EventQueryService {

    private final EventRepository eventRepository;

    public EventQueryServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> handle(GetEventByIdQuery query) {
        log.info("Getting event by ID: {}", query.eventId());
        return eventRepository.findById(query.eventId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> handle(GetEventsByRecipientIdQuery query) {
        log.info("Getting events for recipient: {}", query.recipientId().userId());
        return eventRepository.findByRecipientsContaining(query.recipientId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> handle(GetEventsByCreatorIdQuery query) {
        log.info("Getting events created by: {}", query.creatorId().userId());
        return eventRepository.findByCreatedBy(query.creatorId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> handle(GetAllEventsQuery query) {
        log.info("Getting all events");
        return eventRepository.findAll();
    }
}


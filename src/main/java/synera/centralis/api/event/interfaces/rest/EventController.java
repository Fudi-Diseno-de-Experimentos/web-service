package synera.centralis.api.event.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.commands.DeleteEventCommand;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.domain.model.queries.*;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.event.domain.services.EventQueryService;
import synera.centralis.api.event.interfaces.rest.resources.*;
import synera.centralis.api.event.interfaces.rest.transform.EventResourceFromEntityAssembler;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * EventController handles HTTP requests for Event operations.
 * Provides full CRUD operations for event management.
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(value = "/api/v1/events", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Events", description = "Event Management Endpoints")
public class EventController {

    private final EventCommandService eventCommandService;
    private final EventQueryService eventQueryService;

    public EventController(EventCommandService eventCommandService, EventQueryService eventQueryService) {
        this.eventCommandService = eventCommandService;
        this.eventQueryService = eventQueryService;
    }

    /**
     * Creates a new event.
     */

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new event", description = "Creates a new business event with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventResource> createEvent(Authentication authentication, @Valid @RequestBody CreateEventResource resource) {
        try {
            // Preferir el userId del Authentication si está disponible y es un UUID.
            UUID createdByUuid = null;
            if (authentication != null && authentication.getName() != null) {
                try {
                    createdByUuid = UUID.fromString(authentication.getName());
                } catch (IllegalArgumentException ignored) {
                    // Si el nombre del principal no es UUID, caeremos al valor enviado en el body.
                }
            }

            if (createdByUuid == null) {
                createdByUuid = resource.createdBy();
            }

            var createEventCommand = new CreateEventCommand(
                    resource.title(),
                    resource.description(),
                    resource.date(),
                    resource.location(),
                    resource.recipientIds(),
                    new UserId(createdByUuid)
            );

            Optional<Event> event = eventCommandService.handle(createEventCommand);

            if (event.isPresent()) {
                var eventResource = EventResourceFromEntityAssembler.toResourceFromEntity(event.get());
                return new ResponseEntity<>(eventResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves an event by its ID.
     */
    @GetMapping("/{eventId}")
    @Operation(summary = "Get event by ID", description = "Retrieves a specific event by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventResource> getEventById(
            @Parameter(description = "Event ID", required = true) @PathVariable UUID eventId) {
        try {
            var getEventByIdQuery = new GetEventByIdQuery(eventId);
            Optional<Event> event = eventQueryService.handle(getEventByIdQuery);

            if (event.isPresent()) {
                var eventResource = EventResourceFromEntityAssembler.toResourceFromEntity(event.get());
                return new ResponseEntity<>(eventResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all events (for list view).
     * Can be filtered by userId parameter to get events for a specific user.
     */
    @GetMapping
    @Operation(summary = "Get all events or filter by user", description = "Retrieves all events or events for a specific user (as recipient or creator)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventResource>> getEvents(
            @Parameter(description = "User ID to filter events (optional)") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Filter type: 'recipient' or 'creator' (optional)") @RequestParam(required = false) String filterType) {
        try {
            List<Event> events;

            if (userId != null && "recipient".equalsIgnoreCase(filterType)) {
                // Get events where user is a recipient
                var query = new GetEventsByRecipientIdQuery(new UserId(userId));
                events = eventQueryService.handle(query);
            } else if (userId != null && "creator".equalsIgnoreCase(filterType)) {
                // Get events created by user
                var query = new GetEventsByCreatorIdQuery(new UserId(userId));
                events = eventQueryService.handle(query);
            } else if (userId != null) {
                // Default: get events where user is a recipient
                var query = new GetEventsByRecipientIdQuery(new UserId(userId));
                events = eventQueryService.handle(query);
            } else {
                // Get all events
                var query = new GetAllEventsQuery();
                events = eventQueryService.handle(query);
            }

            var eventResources = events.stream()
                    .map(EventResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return new ResponseEntity<>(eventResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves events in calendar format.
     * Returns the same data as the list view but can be used by frontend to render calendar.
     */
    @GetMapping("/calendar")
    @Operation(summary = "Get events for calendar view", description = "Retrieves events formatted for calendar display")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventResource>> getEventsForCalendar(
            @Parameter(description = "User ID to filter events (optional)") @RequestParam(required = false) UUID userId) {
        try {
            List<Event> events;

            if (userId != null) {
                // Get events where user is a recipient (employees see their events)
                var query = new GetEventsByRecipientIdQuery(new UserId(userId));
                events = eventQueryService.handle(query);
            } else {
                // Get all events
                var query = new GetAllEventsQuery();
                events = eventQueryService.handle(query);
            }

            var eventResources = events.stream()
                    .map(EventResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return new ResponseEntity<>(eventResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an event's information.
     */
    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update event", description = "Updates event information including title, description, date, location, and recipients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventResource> updateEvent(
            @Parameter(description = "Event ID", required = true) @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventResource resource) {
        try {
            var updateEventCommand = new UpdateEventCommand(
                    eventId,
                    resource.title(),
                    resource.description(),
                    resource.date(),
                    resource.location(),
                    resource.recipientIds()
            );
            Optional<Event> updatedEvent = eventCommandService.handle(updateEventCommand);

            if (updatedEvent.isPresent()) {
                var eventResource = EventResourceFromEntityAssembler.toResourceFromEntity(updatedEvent.get());
                return new ResponseEntity<>(eventResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an event.
     */
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete event", description = "Permanently deletes an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "Event ID", required = true) @PathVariable UUID eventId) {
        try {
            var deleteEventCommand = new DeleteEventCommand(eventId);
            Optional<UUID> deletedEventId = eventCommandService.handle(deleteEventCommand);

            if (deletedEventId.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

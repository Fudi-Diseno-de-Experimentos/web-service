package synera.centralis.api.dashboard.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.dashboard.domain.model.queries.*;
import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;
import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;
import synera.centralis.api.dashboard.domain.services.ContentViewQueryService;
import synera.centralis.api.dashboard.interfaces.rest.resources.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Dashboard endpoints (management queries)
 * 
 * All endpoints are accessible to any authenticated user - no role restrictions
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoints for dashboard analytics and management queries")
public class DashboardController {

    private final ContentViewQueryService contentViewQueryService;

    public DashboardController(ContentViewQueryService contentViewQueryService) {
        this.contentViewQueryService = contentViewQueryService;
    }

    @GetMapping("/users/{userId}/announcements/views")
    @Operation(
        summary = "Get announcements viewed by user",
        description = "Retrieves all announcements that a specific user has viewed. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user's announcement views",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAnnouncementViewResource.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid user ID format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<List<UserAnnouncementViewResource>> getUserAnnouncementViews(
            @Parameter(description = "UUID of the user", required = true)
            @PathVariable UUID userId) {
        
        GetUserAnnouncementViewsQuery query = new GetUserAnnouncementViewsQuery(new UserId(userId));
        List<UserAnnouncementViewResource> views = contentViewQueryService.handle(query);
        return ResponseEntity.ok(views);
    }

    @GetMapping("/announcements/{announcementId}/users/views")
    @Operation(
        summary = "Get users who viewed announcement",
        description = "Retrieves all users who have viewed a specific announcement. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved announcement viewers",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementViewerResource.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid announcement ID format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        )
    })
    public ResponseEntity<List<AnnouncementViewerResource>> getAnnouncementViewers(
            @Parameter(description = "UUID of the announcement", required = true)
            @PathVariable UUID announcementId) {
        
        GetAnnouncementViewersQuery query = new GetAnnouncementViewersQuery(new AnnouncementId(announcementId));
        List<AnnouncementViewerResource> viewers = contentViewQueryService.handle(query);
        return ResponseEntity.ok(viewers);
    }

    @GetMapping("/users/{userId}/events/views")
    @Operation(
        summary = "Get events viewed by user",
        description = "Retrieves all events that a specific user has viewed. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user's event views",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEventViewResource.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid user ID format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<List<UserEventViewResource>> getUserEventViews(
            @Parameter(description = "UUID of the user", required = true)
            @PathVariable UUID userId) {
        
        GetUserEventViewsQuery query = new GetUserEventViewsQuery(new UserId(userId));
        List<UserEventViewResource> views = contentViewQueryService.handle(query);
        return ResponseEntity.ok(views);
    }

    @GetMapping("/events/{eventId}/users/views")
    @Operation(
        summary = "Get users who viewed event",
        description = "Retrieves all users who have viewed a specific event. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved event viewers",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventViewerResource.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid event ID format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Event not found"
        )
    })
    public ResponseEntity<List<EventViewerResource>> getEventViewers(
            @Parameter(description = "UUID of the event", required = true)
            @PathVariable UUID eventId) {
        
        GetEventViewersQuery query = new GetEventViewersQuery(new EventId(eventId));
        List<EventViewerResource> viewers = contentViewQueryService.handle(query);
        return ResponseEntity.ok(viewers);
    }

    @GetMapping("/views/summary")
    @Operation(
        summary = "Get views summary",
        description = "Retrieves a general summary with metrics of announcement and event views. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved views summary",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ViewsSummaryResource.class))
        )
    })
    public ResponseEntity<ViewsSummaryResource> getViewsSummary() {
        GetViewsSummaryQuery query = new GetViewsSummaryQuery();
        ViewsSummaryResource summary = contentViewQueryService.handle(query);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/announcements/{announcementId}/stats")
    @Operation(
        summary = "Get announcement view statistics",
        description = "Retrieves view statistics for a specific announcement in pie chart format. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved announcement statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementStatsResource.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid announcement ID format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        )
    })
    public ResponseEntity<AnnouncementStatsResource> getAnnouncementStats(
            @Parameter(description = "UUID of the announcement", required = true)
            @PathVariable UUID announcementId) {
        
        GetAnnouncementStatsQuery query = new GetAnnouncementStatsQuery(new AnnouncementId(announcementId));
        AnnouncementStatsResource stats = contentViewQueryService.handle(query);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/events/{eventId}/stats")
    @Operation(
        summary = "Get event view statistics",
        description = "Retrieves view statistics for a specific event in pie chart format. Accessible to any authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved event statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventStatsResource.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid event ID format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Event not found"
        )
    })
    public ResponseEntity<EventStatsResource> getEventStats(
            @Parameter(description = "UUID of the event", required = true)
            @PathVariable UUID eventId) {
        
        GetEventStatsQuery query = new GetEventStatsQuery(new EventId(eventId));
        EventStatsResource stats = contentViewQueryService.handle(query);
        return ResponseEntity.ok(stats);
    }
}
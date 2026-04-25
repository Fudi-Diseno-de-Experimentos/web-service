package synera.centralis.api.dashboard.interfaces.rest.transform;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.dashboard.domain.model.commands.RegisterAnnouncementViewCommand;
import synera.centralis.api.dashboard.domain.model.commands.RegisterEventViewCommand;
import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;
import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;
import synera.centralis.api.dashboard.domain.services.ContentViewCommandService;
import synera.centralis.api.dashboard.interfaces.rest.resources.RegisterViewResource;
import synera.centralis.api.dashboard.interfaces.rest.resources.ViewRegistrationResponseResource;

import java.time.LocalDateTime;

/**
 * REST Controller for Analytics endpoints (tracking visualizations)
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Endpoints for tracking content visualizations")
public class AnalyticsController {

    private final ContentViewCommandService contentViewCommandService;

    public AnalyticsController(ContentViewCommandService contentViewCommandService) {
        this.contentViewCommandService = contentViewCommandService;
    }

    @PostMapping("/announcements/{announcementId}/views")
    @Operation(
        summary = "Register announcement view", 
        description = "Registers that a user has viewed an announcement. Implements 'one view per content per user forever' rule to prevent duplicates."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "New view registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ViewRegistrationResponseResource.class))
        ),
        @ApiResponse(
            responseCode = "200", 
            description = "User already viewed this announcement - existing view returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ViewRegistrationResponseResource.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized"
        )
    })
    public ResponseEntity<ViewRegistrationResponseResource> registerAnnouncementView(
            @Parameter(description = "Announcement ID", required = true)
            @PathVariable String announcementId,
            @Valid @RequestBody RegisterViewResource resource
    ) {
        var command = new RegisterAnnouncementViewCommand(
            new UserId(resource.userId()),
            new AnnouncementId(announcementId)
        );

        var contentView = contentViewCommandService.handle(command);
        
        if (contentView.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var view = contentView.get();
        boolean isNewView = view.getViewDateTime().isAfter(LocalDateTime.now().minusSeconds(5));

        var response = new ViewRegistrationResponseResource(
            view.getId().toString(),
            view.getUserId().value().toString(),
            view.getContentId().toString(),
            view.getViewDateTime(),
            isNewView ? "Announcement view registered successfully" : "User already viewed this announcement",
            isNewView
        );

        return ResponseEntity
            .status(isNewView ? HttpStatus.CREATED : HttpStatus.OK)
            .body(response);
    }

    @PostMapping("/events/{eventId}/views")
    @Operation(
        summary = "Register event view", 
        description = "Registers that a user has viewed an event. Implements 'one view per content per user forever' rule to prevent duplicates."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "New view registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ViewRegistrationResponseResource.class))
        ),
        @ApiResponse(
            responseCode = "200", 
            description = "User already viewed this event - existing view returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ViewRegistrationResponseResource.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized"
        )
    })
    public ResponseEntity<ViewRegistrationResponseResource> registerEventView(
            @Parameter(description = "Event ID", required = true)
            @PathVariable String eventId,
            @Valid @RequestBody RegisterViewResource resource
    ) {
        var command = new RegisterEventViewCommand(
            new UserId(resource.userId()),
            new EventId(eventId)
        );

        var contentView = contentViewCommandService.handle(command);
        
        if (contentView.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var view = contentView.get();
        boolean isNewView = view.getViewDateTime().isAfter(LocalDateTime.now().minusSeconds(5));

        var response = new ViewRegistrationResponseResource(
            view.getId().toString(),
            view.getUserId().value().toString(),
            view.getContentId().toString(),
            view.getViewDateTime(),
            isNewView ? "Event view registered successfully" : "User already viewed this event",
            isNewView
        );

        return ResponseEntity
            .status(isNewView ? HttpStatus.CREATED : HttpStatus.OK)
            .body(response);
    }
}
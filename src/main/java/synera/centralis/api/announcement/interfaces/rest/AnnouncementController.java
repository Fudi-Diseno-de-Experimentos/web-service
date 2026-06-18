package synera.centralis.api.announcement.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.announcement.domain.model.commands.DeleteAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.queries.*;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.announcement.domain.services.AnnouncementCommandService;
import synera.centralis.api.announcement.domain.services.AnnouncementQueryService;
import synera.centralis.api.announcement.interfaces.rest.resources.AnnouncementResource;
import synera.centralis.api.announcement.interfaces.rest.resources.CreateAnnouncementResource;
import synera.centralis.api.announcement.interfaces.rest.resources.UpdateAnnouncementResource;
import synera.centralis.api.announcement.interfaces.rest.transform.AnnouncementResourceFromEntityAssembler;
import synera.centralis.api.announcement.interfaces.rest.transform.CreateAnnouncementCommandFromResourceAssembler;
import synera.centralis.api.announcement.interfaces.rest.transform.UpdateAnnouncementCommandFromResourceAssembler;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;

import java.util.UUID;

/**
 * Announcement Controller
 * REST controller for announcement operations
 */
@RestController
@RequestMapping("/api/v1/announcements")
@Tag(name = "Announcements", description = "Operations related to announcements management")
public class AnnouncementController {

    private final AnnouncementCommandService announcementCommandService;
    private final AnnouncementQueryService announcementQueryService;
    private final IamContextFacade iamContextFacade;

    @Autowired
    public AnnouncementController(AnnouncementCommandService announcementCommandService,
                                AnnouncementQueryService announcementQueryService,
                                IamContextFacade iamContextFacade) {
        this.announcementCommandService = announcementCommandService;
        this.announcementQueryService = announcementQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    private CompanyId getCurrentCompanyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedException("User not authenticated");
        }
        String username = authentication.getName();
        java.util.UUID companyId = iamContextFacade.fetchCompanyIdByUsername(username);
        if (companyId == null) {
            throw new UnauthorizedException("User not associated with a company");
        }
        return new CompanyId(companyId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create a new announcement", description = "Creates a new announcement in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Announcement created successfully",
                content = @Content(schema = @Schema(implementation = AnnouncementResource.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody CreateAnnouncementResource resource) {
        var companyId = getCurrentCompanyId();
        var command = CreateAnnouncementCommandFromResourceAssembler.toCommandFromResource(resource, companyId);
        var announcement = announcementCommandService.handle(command);
        
        var announcementResource = AnnouncementResourceFromEntityAssembler.toResourceFromEntity(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementResource);
    }

    @GetMapping("/{announcementId}")
    @Operation(summary = "Get announcement by ID", description = "Retrieves a specific announcement by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcement found",
                content = @Content(schema = @Schema(implementation = AnnouncementResource.class))),
        @ApiResponse(responseCode = "404", description = "Announcement not found",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getAnnouncementById(
            @Parameter(description = "Unique identifier of the announcement") @PathVariable UUID announcementId) {
        var companyId = getCurrentCompanyId();
        var query = new GetAnnouncementByIdQuery(announcementId, companyId);
        var announcement = announcementQueryService.handle(query)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id: " + announcementId));
        
        var resource = AnnouncementResourceFromEntityAssembler.toResourceFromEntity(announcement);
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    @Operation(summary = "Get all announcements", description = "Retrieves all announcements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcements retrieved successfully")
    })
    public ResponseEntity<List<AnnouncementResource>> getAllAnnouncements() {
        var companyId = getCurrentCompanyId();
        var query = new GetAllAnnouncementsQuery(companyId);
        var announcements = announcementQueryService.handle(query);
        var resources = announcements.stream()
                .map(AnnouncementResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get announcements by priority", description = "Retrieves announcements filtered by priority level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcements retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid priority level")
    })
    public ResponseEntity<?> getAnnouncementsByPriority(
            @Parameter(description = "Priority level (NORMAL, HIGH, URGENT)") @PathVariable String priority) {
        
        var companyId = getCurrentCompanyId();
        var priorityLevel = Priority.PriorityLevel.valueOf(priority.toUpperCase());
        var query = new GetAnnouncementsByPriorityQuery(priorityLevel, companyId);
        
        var announcements = announcementQueryService.handle(query);
        var resources = announcements.stream()
                .map(AnnouncementResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/creator/{createdBy}")
    @Operation(summary = "Get announcements by creator", description = "Retrieves announcements created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcements retrieved successfully")
    })
    public ResponseEntity<List<AnnouncementResource>> getAnnouncementsByCreator(
            @Parameter(description = "ID of the user who created the announcements") @PathVariable UUID createdBy) {
        var companyId = getCurrentCompanyId();
        var query = new GetAnnouncementsByCreatorQuery(createdBy, companyId);
        var announcements = announcementQueryService.handle(query);
        var resources = announcements.stream()
                .map(AnnouncementResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{announcementId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update announcement", description = "Updates an existing announcement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Announcement updated successfully",
                content = @Content(schema = @Schema(implementation = AnnouncementResource.class))),
        @ApiResponse(responseCode = "404", description = "Announcement not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> updateAnnouncement(
            @Parameter(description = "Unique identifier of the announcement") @PathVariable UUID announcementId,
            @Valid @RequestBody UpdateAnnouncementResource resource) {
        
        var companyId = getCurrentCompanyId();
        var command = UpdateAnnouncementCommandFromResourceAssembler.toCommandFromResource(announcementId, resource, companyId);
        var announcement = announcementCommandService.handle(command);
        
        var announcementResource = AnnouncementResourceFromEntityAssembler.toResourceFromEntity(announcement);
        return ResponseEntity.ok(announcementResource);
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete announcement", description = "Deletes an announcement and all its associated comments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Announcement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Announcement not found")
    })
    public ResponseEntity<Void> deleteAnnouncement(
            @Parameter(description = "Unique identifier of the announcement") @PathVariable UUID announcementId) {
        var companyId = getCurrentCompanyId();
        var command = new DeleteAnnouncementCommand(announcementId, companyId);
        announcementCommandService.handle(command);
        
        return ResponseEntity.noContent().build();
    }
}
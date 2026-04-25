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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import synera.centralis.api.announcement.domain.model.commands.DeleteCommentCommand;
import synera.centralis.api.announcement.domain.model.queries.GetCommentByIdQuery;
import synera.centralis.api.announcement.domain.model.queries.GetCommentsByAnnouncementQuery;
import synera.centralis.api.announcement.domain.services.CommentCommandService;
import synera.centralis.api.announcement.domain.services.CommentQueryService;
import synera.centralis.api.announcement.interfaces.rest.resources.CommentResource;
import synera.centralis.api.announcement.interfaces.rest.resources.CreateCommentResource;
import synera.centralis.api.announcement.interfaces.rest.transform.CommentResourceFromEntityAssembler;
import synera.centralis.api.announcement.interfaces.rest.transform.CreateCommentCommandFromResourceAssembler;

import java.util.UUID;

/**
 * Comment Controller
 * REST controller for comment operations
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Comments", description = "Operations related to announcement comments management")
public class CommentController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @Autowired
    public CommentController(CommentCommandService commentCommandService,
                           CommentQueryService commentQueryService) {
        this.commentCommandService = commentCommandService;
        this.commentQueryService = commentQueryService;
    }

    @PostMapping("/announcements/{announcementId}/comments")
    @Operation(summary = "Add comment to announcement", description = "Creates a new comment for a specific announcement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment created successfully",
                content = @Content(schema = @Schema(implementation = CommentResource.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or announcement not found",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> addCommentToAnnouncement(
            @Parameter(description = "Unique identifier of the announcement") @PathVariable UUID announcementId,
            @Valid @RequestBody CreateCommentResource resource) {
        
        try {
            var command = CreateCommentCommandFromResourceAssembler.toCommandFromResource(announcementId, resource);
            var comment = commentCommandService.handle(command);
            
            if (comment.isPresent()) {
                var commentResource = CommentResourceFromEntityAssembler.toResourceFromEntity(comment.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(commentResource);
            }
            
            return ResponseEntity.badRequest().body("Error creating comment");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/comments/{commentId}")
    @Operation(summary = "Get comment by ID", description = "Retrieves a specific comment by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment found",
                content = @Content(schema = @Schema(implementation = CommentResource.class))),
        @ApiResponse(responseCode = "404", description = "Comment not found",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "Unique identifier of the comment") @PathVariable UUID commentId) {
        
        var query = new GetCommentByIdQuery(commentId);
        var comment = commentQueryService.handle(query);
        
        if (comment.isPresent()) {
            var resource = CommentResourceFromEntityAssembler.toResourceFromEntity(comment.get());
            return ResponseEntity.ok(resource);
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/announcements/{announcementId}/comments")
    @Operation(summary = "Get comments by announcement", description = "Retrieves all comments for a specific announcement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    })
    public ResponseEntity<List<CommentResource>> getCommentsByAnnouncement(
            @Parameter(description = "Unique identifier of the announcement") @PathVariable UUID announcementId) {
        
        var query = new GetCommentsByAnnouncementQuery(announcementId);
        var comments = commentQueryService.handle(query);
        var resources = comments.stream()
                .map(CommentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Deletes a comment by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Unique identifier of the comment") @PathVariable UUID commentId) {
        
        var command = new DeleteCommentCommand(commentId);
        boolean deleted = commentCommandService.handle(command);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}
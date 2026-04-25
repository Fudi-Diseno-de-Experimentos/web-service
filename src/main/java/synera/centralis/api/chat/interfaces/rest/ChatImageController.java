package synera.centralis.api.chat.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.chat.domain.model.entities.ChatImage;
import synera.centralis.api.chat.domain.model.commands.CreateChatImageCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteChatImageCommand;
import synera.centralis.api.chat.domain.model.queries.GetChatImageByIdQuery;
import synera.centralis.api.chat.domain.model.queries.GetChatImagesByGroupIdQuery;
import synera.centralis.api.chat.domain.services.ChatImageCommandService;
import synera.centralis.api.chat.domain.services.ChatImageQueryService;
import synera.centralis.api.chat.interfaces.rest.resources.CreateChatImageResource;
import synera.centralis.api.chat.interfaces.rest.resources.ChatImageResource;
import synera.centralis.api.chat.interfaces.rest.transform.CreateChatImageCommandFromResourceAssembler;
import synera.centralis.api.chat.interfaces.rest.transform.ChatImageResourceFromEntityAssembler;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ChatImageController handles HTTP requests for Chat Image operations.
 * Provides endpoints for sharing, retrieving, and deleting images in group chats.
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
@RestController
@RequestMapping(value = "/api/v1/groups/{groupId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Chat Images", description = "Chat Image Management Endpoints")
public class ChatImageController {

    private final ChatImageCommandService chatImageCommandService;
    private final ChatImageQueryService chatImageQueryService;

    public ChatImageController(ChatImageCommandService chatImageCommandService, 
                              ChatImageQueryService chatImageQueryService) {
        this.chatImageCommandService = chatImageCommandService;
        this.chatImageQueryService = chatImageQueryService;
    }

    /**
     * Shares a new image in a group chat.
     */
    @PostMapping
    @Operation(summary = "Share a new image", description = "Shares a new image in the specified group chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image shared successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ChatImageResource> shareImage(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Valid @RequestBody CreateChatImageResource resource) {
        try {
            var createCommand = CreateChatImageCommandFromResourceAssembler.toCommandFromResource(groupId, resource);
            Optional<ChatImage> chatImage = chatImageCommandService.handle(createCommand);
            
            if (chatImage.isPresent()) {
                var chatImageResource = ChatImageResourceFromEntityAssembler.toResourceFromEntity(chatImage.get());
                return new ResponseEntity<>(chatImageResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves an image by its ID.
     */
    @GetMapping("/{imageId}")
    @Operation(summary = "Get image by ID", description = "Retrieves a specific chat image by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ChatImageResource> getImageById(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Image ID", required = true) @PathVariable UUID imageId) {
        try {
            var getImageQuery = new GetChatImageByIdQuery(imageId);
            Optional<ChatImage> chatImage = chatImageQueryService.handle(getImageQuery);
            
            if (chatImage.isPresent()) {
                // Verify the image belongs to the specified group
                if (!chatImage.get().getGroupId().equals(groupId)) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                
                var chatImageResource = ChatImageResourceFromEntityAssembler.toResourceFromEntity(chatImage.get());
                return new ResponseEntity<>(chatImageResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all images for a specific group.
     */
    @GetMapping
    @Operation(summary = "Get all images in group", description = "Retrieves all images shared in the specified group chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ChatImageResource>> getImagesByGroupId(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId) {
        try {
            var getImagesQuery = new GetChatImagesByGroupIdQuery(groupId);
            List<ChatImage> chatImages = chatImageQueryService.handle(getImagesQuery);
            
            List<ChatImageResource> chatImageResources = chatImages.stream()
                    .map(ChatImageResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            
            return new ResponseEntity<>(chatImageResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an image by its ID.
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete image", description = "Deletes a specific chat image by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ChatImageResource> deleteImage(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Image ID", required = true) @PathVariable UUID imageId) {
        try {
            // First verify the image exists and belongs to the group
            var getImageQuery = new GetChatImageByIdQuery(imageId);
            Optional<ChatImage> existingImage = chatImageQueryService.handle(getImageQuery);
            
            if (existingImage.isEmpty() || !existingImage.get().getGroupId().equals(groupId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            var deleteCommand = new DeleteChatImageCommand(imageId);
            Optional<ChatImage> deletedImage = chatImageCommandService.handle(deleteCommand);
            
            if (deletedImage.isPresent()) {
                var chatImageResource = ChatImageResourceFromEntityAssembler.toResourceFromEntity(deletedImage.get());
                return new ResponseEntity<>(chatImageResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
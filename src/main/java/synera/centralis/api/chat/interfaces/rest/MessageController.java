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
import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.domain.model.commands.*;
import synera.centralis.api.chat.domain.model.queries.*;
import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;
import synera.centralis.api.chat.domain.services.MessageCommandService;
import synera.centralis.api.chat.domain.services.MessageQueryService;
import synera.centralis.api.chat.interfaces.rest.resources.*;
import synera.centralis.api.chat.interfaces.rest.transform.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * MessageController handles HTTP requests for Message operations.
 * Provides full CRUD operations for message management within groups.
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
@RestController
@RequestMapping(value = "/api/v1/groups/{groupId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Messages", description = "Message Management Endpoints")
public class MessageController {

    private final MessageCommandService messageCommandService;
    private final MessageQueryService messageQueryService;

    public MessageController(MessageCommandService messageCommandService, MessageQueryService messageQueryService) {
        this.messageCommandService = messageCommandService;
        this.messageQueryService = messageQueryService;
    }

    /**
     * Creates a new message in a group.
     */
    @PostMapping
    @Operation(summary = "Create a new message", description = "Creates a new message in the specified group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MessageResource> createMessage(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Valid @RequestBody CreateMessageResource resource) {
        try {
            var createMessageCommand = CreateMessageCommandFromResourceAssembler.toCommandFromResource(groupId, resource);
            Optional<Message> message = messageCommandService.handle(createMessageCommand);
            
            if (message.isPresent()) {
                var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(message.get());
                return new ResponseEntity<>(messageResource, HttpStatus.CREATED);
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
     * Retrieves a message by its ID.
     */
    @GetMapping("/{messageId}")
    @Operation(summary = "Get message by ID", description = "Retrieves a specific message by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MessageResource> getMessageById(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Message ID", required = true) @PathVariable UUID messageId) {
        try {
            var getMessageByIdQuery = new GetMessageByIdQuery(messageId);
            Optional<Message> message = messageQueryService.handle(getMessageByIdQuery);
            
            if (message.isPresent()) {
                // Verify the message belongs to the specified group
                if (!message.get().getGroupId().equals(groupId)) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                
                var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(message.get());
                return new ResponseEntity<>(messageResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all messages in a group.
     */
    @GetMapping
    @Operation(summary = "Get messages by group", description = "Retrieves all messages in the specified group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MessageResource>> getMessagesByGroupId(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId) {
        try {
            var getMessagesByGroupIdQuery = new GetMessagesByGroupIdQuery(groupId);
            List<Message> messages = messageQueryService.handle(getMessagesByGroupIdQuery);
            
            var messageResources = messages.stream()
                    .map(MessageResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            
            return new ResponseEntity<>(messageResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves messages by status in a group.
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get messages by status", description = "Retrieves messages in the group filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status parameter"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<MessageResource>> getMessagesByStatus(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Message status (SENT/EDITED/DELETED)", required = true) @PathVariable MessageStatus status) {
        try {
            var getMessagesByStatusQuery = new GetMessagesByStatusQuery(status);
            List<Message> messages = messageQueryService.handle(getMessagesByStatusQuery);
            
            // Filter messages by group ID
            var groupMessages = messages.stream()
                    .filter(message -> message.getGroupId().equals(groupId))
                    .map(MessageResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            
            return new ResponseEntity<>(groupMessages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a message's body content.
     */
    @PutMapping("/{messageId}")
    @Operation(summary = "Update message body", description = "Updates the content of an existing message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message updated successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot edit this message"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MessageResource> updateMessageBody(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Message ID", required = true) @PathVariable UUID messageId,
            @Valid @RequestBody UpdateMessageBodyResource resource) {
        try {
            // First verify the message belongs to the group
            var getMessageQuery = new GetMessageByIdQuery(messageId);
            Optional<Message> existingMessage = messageQueryService.handle(getMessageQuery);
            
            if (existingMessage.isEmpty() || !existingMessage.get().getGroupId().equals(groupId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            var updateMessageBodyCommand = UpdateMessageBodyCommandFromResourceAssembler.toCommandFromResource(messageId, resource);
            Optional<Message> updatedMessage = messageCommandService.handle(updateMessageBodyCommand);
            
            if (updatedMessage.isPresent()) {
                var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(updatedMessage.get());
                return new ResponseEntity<>(messageResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a message's status.
     */
    @PatchMapping("/{messageId}/status")
    @Operation(summary = "Update message status", description = "Changes the status of a message (SENT/EDITED/DELETED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MessageResource> updateMessageStatus(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Message ID", required = true) @PathVariable UUID messageId,
            @Valid @RequestBody UpdateMessageStatusResource resource) {
        try {
            // First verify the message belongs to the group
            var getMessageQuery = new GetMessageByIdQuery(messageId);
            Optional<Message> existingMessage = messageQueryService.handle(getMessageQuery);
            
            if (existingMessage.isEmpty() || !existingMessage.get().getGroupId().equals(groupId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            var updateMessageStatusCommand = UpdateMessageStatusCommandFromResourceAssembler.toCommandFromResource(messageId, resource);
            Optional<Message> updatedMessage = messageCommandService.handle(updateMessageStatusCommand);
            
            if (updatedMessage.isPresent()) {
                var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(updatedMessage.get());
                return new ResponseEntity<>(messageResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a message.
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "Delete message", description = "Permanently deletes a message from the group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Message ID", required = true) @PathVariable UUID messageId) {
        try {
            // First verify the message belongs to the group
            var getMessageQuery = new GetMessageByIdQuery(messageId);
            Optional<Message> existingMessage = messageQueryService.handle(getMessageQuery);
            
            if (existingMessage.isEmpty() || !existingMessage.get().getGroupId().equals(groupId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            var deleteMessageCommand = new DeleteMessageCommand(messageId);
            Optional<UUID> deletedMessageId = messageCommandService.handle(deleteMessageCommand);
            
            if (deletedMessageId.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
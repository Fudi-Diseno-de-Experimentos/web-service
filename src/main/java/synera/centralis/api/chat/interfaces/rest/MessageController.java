package synera.centralis.api.chat.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;

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
    private final IamContextFacade iamContextFacade;

    public MessageController(MessageCommandService messageCommandService, MessageQueryService messageQueryService, synera.centralis.api.iam.interfaces.acl.IamContextFacade iamContextFacade) {
        this.messageCommandService = messageCommandService;
        this.messageQueryService = messageQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    private CompanyId getCurrentCompanyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        String username = authentication.getName();
        java.util.UUID companyId = iamContextFacade.fetchCompanyIdByUsername(username);
        if (companyId == null) {
            throw new UnauthorizedException("User not associated with any company");
        }
        return new CompanyId(companyId);
    }

    /**
     * Remitente derivado SIEMPRE del JWT, nunca del cuerpo, para impedir
     * suplantación (mismo criterio que el controlador WebSocket).
     */
    private UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedException("Authentication required");
        }
        UUID userId = iamContextFacade.fetchUserIdByUsername(authentication.getName());
        if (userId == null) {
            throw new UnauthorizedException("Authenticated user not found");
        }
        return userId;
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
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        // El remitente se deriva del JWT; resource.senderId() se ignora a
        // propósito para impedir suplantación. La pertenencia al grupo la
        // valida MessageCommandServiceImpl.
        UUID senderId = getCurrentUserId();
        var createMessageCommand = CreateMessageCommandFromResourceAssembler.toCommandFromResource(groupId, senderId, resource);
        var message = messageCommandService.handle(createMessageCommand);
        
        var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(message);
        return new ResponseEntity<>(messageResource, HttpStatus.CREATED);
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
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var getMessageByIdQuery = new GetMessageByIdQuery(messageId);
        var message = messageQueryService.handle(getMessageByIdQuery)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        // Verify the message belongs to the specified group
        if (!message.getGroupId().equals(groupId)) {
            throw new ResourceNotFoundException("Message not found in this group");
        }
        
        var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(message);
        return new ResponseEntity<>(messageResource, HttpStatus.OK);
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
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var getMessagesByGroupIdQuery = new GetMessagesByGroupIdQuery(groupId, companyId);
        List<Message> messages = messageQueryService.handle(getMessagesByGroupIdQuery);
        
        var messageResources = messages.stream()
                .map(MessageResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return new ResponseEntity<>(messageResources, HttpStatus.OK);
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
        var getMessagesByStatusQuery = new GetMessagesByStatusQuery(status);
        List<Message> messages = messageQueryService.handle(getMessagesByStatusQuery);
        
        // Filter messages by group ID
        var groupMessages = messages.stream()
                .filter(message -> message.getGroupId().equals(groupId))
                .map(MessageResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return new ResponseEntity<>(groupMessages, HttpStatus.OK);
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
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        // First verify the message belongs to the group
        var getMessageQuery = new GetMessageByIdQuery(messageId);
        var existingMessage = messageQueryService.handle(getMessageQuery)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        if (!existingMessage.getGroupId().equals(groupId)) {
            throw new ResourceNotFoundException("Message not found in this group");
        }
        
        var updateMessageBodyCommand = UpdateMessageBodyCommandFromResourceAssembler.toCommandFromResource(messageId, resource);
        var updatedMessage = messageCommandService.handle(updateMessageBodyCommand);
        
        var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(updatedMessage);
        return new ResponseEntity<>(messageResource, HttpStatus.OK);
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
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        // First verify the message belongs to the group
        var getMessageQuery = new GetMessageByIdQuery(messageId);
        var existingMessage = messageQueryService.handle(getMessageQuery)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        if (!existingMessage.getGroupId().equals(groupId)) {
            throw new ResourceNotFoundException("Message not found in this group");
        }
        
        var updateMessageStatusCommand = UpdateMessageStatusCommandFromResourceAssembler.toCommandFromResource(messageId, resource);
        var updatedMessage = messageCommandService.handle(updateMessageStatusCommand);
        
        var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(updatedMessage);
        return new ResponseEntity<>(messageResource, HttpStatus.OK);
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
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        // First verify the message belongs to the group
        var getMessageQuery = new GetMessageByIdQuery(messageId);
        var existingMessage = messageQueryService.handle(getMessageQuery)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        if (!existingMessage.getGroupId().equals(groupId)) {
            throw new ResourceNotFoundException("Message not found in this group");
        }
        
        var deleteMessageCommand = new DeleteMessageCommand(messageId);
        messageCommandService.handle(deleteMessageCommand);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
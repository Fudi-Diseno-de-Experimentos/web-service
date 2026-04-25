package synera.centralis.api.notification.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.commands.UpdateNotificationStatusCommand;
import synera.centralis.api.notification.domain.model.queries.GetNotificationByIdQuery;
import synera.centralis.api.notification.domain.model.queries.GetNotificationsByUserIdQuery;
import synera.centralis.api.notification.domain.model.queries.GetNotificationStatusQuery;
import synera.centralis.api.notification.domain.services.NotificationCommandService;
import synera.centralis.api.notification.domain.services.NotificationQueryService;
import synera.centralis.api.notification.interfaces.rest.resources.CreateNotificationResource;
import synera.centralis.api.notification.interfaces.rest.resources.NotificationResource;
import synera.centralis.api.notification.interfaces.rest.resources.NotificationStatusResource;
import synera.centralis.api.notification.interfaces.rest.resources.UpdateNotificationStatusResource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {
    
    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;
    
    public NotificationController(NotificationQueryService notificationQueryService,
                                NotificationCommandService notificationCommandService) {
        this.notificationQueryService = notificationQueryService;
        this.notificationCommandService = notificationCommandService;
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get notifications for a user", description = "Retrieve all notifications for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<NotificationResource>> getNotificationsByUserId(
            @Parameter(description = "User ID to get notifications for", required = true)
            @PathVariable String userId) {
        
        var query = new GetNotificationsByUserIdQuery(userId);
        var notifications = notificationQueryService.handle(query);
        
        var resources = notifications.stream()
                .map(notification -> new NotificationResource(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getRecipients(),
                        notification.getPriority(),
                        notification.getStatus(),
                        LocalDateTime.ofInstant(notification.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(notification.getUpdatedAt().toInstant(), ZoneId.systemDefault())
                ))
                .toList();
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{id}/status")
    @Operation(summary = "Get notification status", description = "Retrieve the status of a specific notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notification status"),
            @ApiResponse(responseCode = "400", description = "Invalid notification ID"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationStatusResource> getNotificationStatus(
            @Parameter(description = "Notification ID to get status for", required = true)
            @PathVariable UUID id) {
        
        var query = new GetNotificationStatusQuery(id);
        var notificationOpt = notificationQueryService.handle(query);
        
        if (notificationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var notification = notificationOpt.get();
        var resource = new NotificationStatusResource(
                notification.getId(),
                notification.getStatus()
        );
        
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/notification/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieve a specific notification with all details including recipients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationResource> getNotificationById(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable UUID id) {
        
        var query = new GetNotificationByIdQuery(id);
        var notificationOpt = notificationQueryService.handle(query);
        
        if (notificationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var notification = notificationOpt.get();
        var resource = new NotificationResource(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRecipients(), // This will show the list of recipient IDs
                notification.getPriority(),
                notification.getStatus(),
                LocalDateTime.ofInstant(notification.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(notification.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
        
        return ResponseEntity.ok(resource);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update notification status", description = "Update the status of a specific notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated notification status"),
            @ApiResponse(responseCode = "400", description = "Invalid notification ID or status"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationStatusResource> updateNotificationStatus(
            @Parameter(description = "Notification ID to update", required = true)
            @PathVariable UUID id,
            @Parameter(description = "New status for the notification", required = true)
            @Valid @RequestBody UpdateNotificationStatusResource resource) {
        
        var command = new UpdateNotificationStatusCommand(id, resource.status());
        var notificationOpt = notificationCommandService.handle(command);
        
        if (notificationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var notification = notificationOpt.get();
        var statusResource = new NotificationStatusResource(
                notification.getId(),
                notification.getStatus()
        );
        
        return ResponseEntity.ok(statusResource);
    }
    
    @PostMapping
    @Operation(summary = "Create notification", description = "Create a new notification for testing purposes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid notification data")
    })
    public ResponseEntity<NotificationResource> createNotification(
            @Parameter(description = "Notification data", required = true)
            @Valid @RequestBody CreateNotificationResource resource) {
        
        var command = new CreateNotificationCommand(
                resource.title(),
                resource.message(),
                resource.recipientIds(),
                resource.priority()
        );
        var notificationOpt = notificationCommandService.handle(command);
        
        if (notificationOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var notification = notificationOpt.get();
        var notificationResource = new NotificationResource(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRecipients(),
                notification.getPriority(),
                notification.getStatus(),
                LocalDateTime.ofInstant(notification.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(notification.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
        
        return ResponseEntity.status(201).body(notificationResource);
    }
}
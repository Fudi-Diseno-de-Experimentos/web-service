package synera.centralis.api.notification.interfaces.rest.transform;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.notification.infrastructure.messaging.fcm.FCMTokenService;
import synera.centralis.api.notification.interfaces.rest.resources.FcmTokenResource;
import synera.centralis.api.notification.interfaces.rest.resources.RegisterFcmTokenResource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "FCM Tokens", description = "Firebase Cloud Messaging token management endpoints")
public class FCMTokenController {
    
    private final FCMTokenService fcmTokenService;
    
    public FCMTokenController(FCMTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }
    
    @PostMapping("/{userId}/fcm-token")
    @Operation(summary = "Register FCM token", description = "Register or update FCM token for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM token registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<FcmTokenResource> registerFcmToken(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "FCM token data", required = true)
            @Valid @RequestBody RegisterFcmTokenResource resource) {
        
        var savedToken = fcmTokenService.saveTokenForUser(
                userId,
                resource.fcmToken(),
                resource.deviceType(),
                resource.deviceId()
        );
        
        var tokenResource = new FcmTokenResource(
                savedToken.getId(),
                savedToken.getUserId(),
                savedToken.getFcmToken(),
                savedToken.getDeviceType(),
                savedToken.getDeviceId(),
                savedToken.getIsActive(),
                LocalDateTime.ofInstant(savedToken.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(savedToken.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
        
        return ResponseEntity.ok(tokenResource);
    }
    
    @GetMapping("/{userId}/fcm-tokens")
    @Operation(summary = "Get user FCM tokens", description = "Retrieve all active FCM tokens for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved FCM tokens"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<FcmTokenResource>> getUserFcmTokens(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        
        var tokens = fcmTokenService.getActiveTokensForUser(userId);
        
        var tokenResources = tokens.stream()
                .map(token -> new FcmTokenResource(
                        token.getId(),
                        token.getUserId(),
                        token.getFcmToken(),
                        token.getDeviceType(),
                        token.getDeviceId(),
                        token.getIsActive(),
                        LocalDateTime.ofInstant(token.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(token.getUpdatedAt().toInstant(), ZoneId.systemDefault())
                ))
                .toList();
        
        return ResponseEntity.ok(tokenResources);
    }
    
    @DeleteMapping("/{userId}/fcm-token")
    @Operation(summary = "Remove FCM token", description = "Remove a specific FCM token for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM token removed successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    public ResponseEntity<Void> removeFcmToken(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "FCM token to remove", required = true)
            @RequestParam String fcmToken) {
        
        boolean removed = fcmTokenService.removeTokenForUser(userId, fcmToken);
        
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{userId}/fcm-tokens")
    @Operation(summary = "Remove all FCM tokens", description = "Remove all FCM tokens for a user (logout)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All FCM tokens removed successfully")
    })
    public ResponseEntity<Void> removeAllFcmTokens(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        
        fcmTokenService.removeAllTokensForUser(userId);
        return ResponseEntity.ok().build();
    }
}
package synera.centralis.api.notification.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record RegisterFcmTokenResource(
        @NotBlank(message = "FCM token cannot be blank")
        String fcmToken,
        
        String deviceType, // iOS, Android, etc.
        
        String deviceId // Unique device identifier
) {
}
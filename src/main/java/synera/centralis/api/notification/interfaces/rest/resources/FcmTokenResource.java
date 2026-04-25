package synera.centralis.api.notification.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.UUID;

public record FcmTokenResource(
        UUID id,
        String userId,
        String fcmToken,
        String deviceType,
        String deviceId,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
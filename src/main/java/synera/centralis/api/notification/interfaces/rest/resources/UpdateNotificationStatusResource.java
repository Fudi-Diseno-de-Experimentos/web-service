package synera.centralis.api.notification.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;

public record UpdateNotificationStatusResource(
        @NotNull(message = "Status cannot be null")
        NotificationStatus status
) {
}
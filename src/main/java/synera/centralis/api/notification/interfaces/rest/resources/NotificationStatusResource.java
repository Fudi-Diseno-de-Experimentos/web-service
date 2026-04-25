package synera.centralis.api.notification.interfaces.rest.resources;

import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;

import java.util.UUID;

public record NotificationStatusResource(
        UUID id,
        NotificationStatus status
) {
}
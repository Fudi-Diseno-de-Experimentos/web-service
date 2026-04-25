package synera.centralis.api.notification.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;

import java.util.List;

public record CreateNotificationResource(
        @NotBlank(message = "Title cannot be blank")
        String title,
        
        @NotBlank(message = "Message cannot be blank")
        String message,
        
        @NotEmpty(message = "Recipients list cannot be empty")
        List<String> recipientIds, // User IDs as strings
        
        @NotNull(message = "Priority cannot be null")
        NotificationPriority priority
) {
}
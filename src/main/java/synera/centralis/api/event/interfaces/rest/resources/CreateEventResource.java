package synera.centralis.api.event.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resource for creating a new event.
 * Contains all necessary information to create an event.
 */
@Schema(description = "Request to create a new event")
public record CreateEventResource(
        @Schema(description = "Title of the event", example = "Monthly Sales Meeting", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Event title is required")
        @Size(max = 200, message = "Event title cannot exceed 200 characters")
        String title,

        @Schema(description = "Description of the event", example = "Discuss Q1 sales performance and goals", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Event description is required")
        @Size(max = 1000, message = "Event description cannot exceed 1000 characters")
        String description,

        @Schema(description = "Date and time of the event", example = "2025-02-15T14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Event date is required")
        LocalDateTime date,

        @Schema(description = "Location of the event (physical or virtual)", example = "Conference Room A / https://zoom.us/j/123456")
        @Size(max = 500, message = "Event location cannot exceed 500 characters")
        String location,

        @Schema(description = "List of recipient user IDs to notify", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "At least one recipient is required")
        List<UUID> recipientIds,

        @Schema(description = "User ID who is creating the event", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Created by is required")
        UUID createdBy
) {
}

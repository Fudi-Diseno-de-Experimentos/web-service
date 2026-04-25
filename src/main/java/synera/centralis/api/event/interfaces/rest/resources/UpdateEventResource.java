package synera.centralis.api.event.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resource for updating an existing event.
 */
@Schema(description = "Request to update an event")
public record UpdateEventResource(
        @Schema(description = "Title of the event", example = "Monthly Sales Meeting")
        @Size(max = 200, message = "Event title cannot exceed 200 characters")
        String title,

        @Schema(description = "Description of the event", example = "Discuss Q1 sales performance and goals")
        @Size(max = 1000, message = "Event description cannot exceed 1000 characters")
        String description,

        @Schema(description = "Date and time of the event", example = "2025-02-15T14:30:00")
        LocalDateTime date,

        @Schema(description = "Location of the event", example = "Conference Room B")
        @Size(max = 500, message = "Event location cannot exceed 500 characters")
        String location,

        @Schema(description = "List of recipient user IDs")
        List<UUID> recipientIds
) {
}


package synera.centralis.api.event.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resource representing an event for API responses.
 */
@Schema(description = "Event information")
public record EventResource(
        @Schema(description = "Event ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Title of the event", example = "Monthly Sales Meeting")
        String title,

        @Schema(description = "Description of the event", example = "Discuss Q1 sales performance and goals")
        String description,

        @Schema(description = "Date and time of the event", example = "2025-02-15T14:30:00")
        LocalDateTime date,

        @Schema(description = "Location of the event", example = "Conference Room A")
        String location,

        @Schema(description = "User ID who created the event")
        UUID createdBy,

        @Schema(description = "List of recipient user IDs")
        List<UUID> recipientIds,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}
package synera.centralis.api.company.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Company space (room) information")
public record SpaceResource(
        @Schema(description = "Space ID")
        UUID id,

        @Schema(description = "Name of the room", example = "ROOM 1")
        String name,

        @Schema(description = "Description of the room")
        String description,

        @Schema(description = "Owning company ID")
        UUID companyId,

        @Schema(description = "Whether the room is free on the queried day; null when no date was requested")
        Boolean available,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}

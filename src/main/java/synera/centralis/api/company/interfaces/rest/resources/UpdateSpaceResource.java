package synera.centralis.api.company.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update a company space (room). Null fields are left unchanged.")
public record UpdateSpaceResource(
        @Schema(description = "Name of the room", example = "ROOM 30")
        @Size(max = 100, message = "Space name cannot exceed 100 characters")
        String name,

        @Schema(description = "Optional description of the room")
        @Size(max = 500, message = "Space description cannot exceed 500 characters")
        String description
) {
}

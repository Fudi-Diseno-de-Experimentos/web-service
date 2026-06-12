package synera.centralis.api.company.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new company space (room)")
public record CreateSpaceResource(
        @Schema(description = "Name of the room", example = "ROOM 1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Space name is required")
        @Size(max = 100, message = "Space name cannot exceed 100 characters")
        String name,

        @Schema(description = "Optional description of the room", example = "Ground floor, seats 12")
        @Size(max = 500, message = "Space description cannot exceed 500 characters")
        String description
) {
}

package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating group visibility.
 * Contains the new visibility setting for the group.
 */
@Schema(description = "Request to update group visibility")
public record UpdateGroupVisibilityResource(
        @Schema(description = "New visibility of the group", example = "PRIVATE", allowableValues = {"PUBLIC", "PRIVATE"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Group visibility is required")
        String visibility
) {
}
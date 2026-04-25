package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Resource for updating group information (name, description, image).
 * All fields are optional, only provided fields will be updated.
 */
@Schema(description = "Request to update group information")
public record UpdateGroupResource(
        @Schema(description = "New name of the group", example = "Updated Team Name")
        @Size(max = 100, message = "Group name cannot exceed 100 characters")
        String name,
        
        @Schema(description = "New description of the group", example = "Updated team description")
        @Size(max = 500, message = "Group description cannot exceed 500 characters")
        String description,
        
        @Schema(description = "New image URL of the group", example = "https://example.com/new-image.jpg")
        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        String imageUrl
) {
}
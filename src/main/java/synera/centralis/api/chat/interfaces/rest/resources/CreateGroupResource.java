package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Resource for creating a new group.
 * Contains all necessary information to create a group.
 */
@Schema(description = "Request to create a new group")
public record CreateGroupResource(
        @Schema(description = "Name of the group", example = "Development Team", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Group name is required")
        @Size(max = 100, message = "Group name cannot exceed 100 characters")
        String name,
        
        @Schema(description = "Description of the group", example = "Team discussion for development tasks")
        @Size(max = 500, message = "Group description cannot exceed 500 characters")
        String description,
        
        @Schema(description = "Image URL of the group", example = "https://example.com/image.jpg")
        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        String imageUrl,
        
        @Schema(description = "Visibility of the group", example = "PUBLIC", allowableValues = {"PUBLIC", "PRIVATE"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Group visibility is required")
        String visibility,
        
        @Schema(description = "List of member user IDs to add to the group", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "At least one member is required")
        List<UUID> memberIds,
        
        @Schema(description = "User ID who is creating the group", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Created by is required")
        UUID createdBy
) {
}
package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Resource representing a group response.
 * Used for returning group information to clients.
 */
@Schema(description = "Group information")
public record GroupResource(
        @Schema(description = "Unique identifier of the group", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        
        @Schema(description = "Name of the group", example = "Development Team")
        String name,
        
        @Schema(description = "Description of the group", example = "Team discussion for development tasks")
        String description,
        
        @Schema(description = "Image URL of the group", example = "https://example.com/image.jpg")
        String imageUrl,
        
        @Schema(description = "Visibility of the group", example = "PUBLIC")
        String visibility,
        
        @Schema(description = "List of member user IDs")
        List<UUID> memberIds,
        
        @Schema(description = "Number of members in the group", example = "5")
        int memberCount,
        
        @Schema(description = "User ID who created the group")
        UUID createdBy,
        
        @Schema(description = "Date when the group was created")
        Date createdAt,
        
        @Schema(description = "Date when the group was last updated")
        Date updatedAt
) {
}
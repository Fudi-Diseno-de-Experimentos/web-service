package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.UUID;

/**
 * Resource representing a chat image response.
 * Used for returning chat image information to clients.
 */
@Schema(description = "Chat image information")
public record ChatImageResource(
        @Schema(description = "Unique identifier of the image", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID imageId,
        
        @Schema(description = "Group ID the image belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID groupId,
        
        @Schema(description = "User ID who shared the image", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID senderId,
        
        @Schema(description = "URL of the shared image", example = "https://example.com/images/photo.jpg")
        String imageUrl,
        
        @Schema(description = "Date when the image was shared")
        Date sentAt,
        
        @Schema(description = "Whether the image is visible (not deleted)", example = "true")
        boolean isVisible
) {
}
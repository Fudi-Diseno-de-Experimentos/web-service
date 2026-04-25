package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Resource for creating a new chat image.
 * Contains all necessary information to share an image in a group chat.
 */
@Schema(description = "Request to share a new image in chat")
public record CreateChatImageResource(
        @Schema(description = "User ID who is sharing the image", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Sender ID is required")
        UUID senderId,
        
        @Schema(description = "URL of the image to share", example = "https://example.com/images/photo.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Image URL is required")
        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        @Pattern(regexp = "^https?://.*", message = "Image URL must be a valid HTTP/HTTPS URL")
        String imageUrl
) {
}
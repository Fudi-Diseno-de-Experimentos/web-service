package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Resource for creating a new message.
 * Contains all necessary information to send a message in a group.
 */
@Schema(description = "Request to create a new message")
public record CreateMessageResource(
        @Schema(description = "User ID who is sending the message", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Sender ID is required")
        UUID senderId,
        
        @Schema(description = "Content of the message", example = "Hello everyone!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Message body is required")
        @Size(max = 1000, message = "Message body cannot exceed 1000 characters")
        String body
) {
}
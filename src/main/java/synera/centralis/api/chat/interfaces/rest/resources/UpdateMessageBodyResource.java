package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for updating message body content.
 * Contains the new body content for the message.
 */
@Schema(description = "Request to update message body")
public record UpdateMessageBodyResource(
        @Schema(description = "New content of the message", example = "Updated message content", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Message body is required")
        @Size(max = 1000, message = "Message body cannot exceed 1000 characters")
        String body
) {
}
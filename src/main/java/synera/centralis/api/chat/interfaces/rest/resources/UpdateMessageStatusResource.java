package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating message status.
 * Contains the new status for the message.
 */
@Schema(description = "Request to update message status")
public record UpdateMessageStatusResource(
        @Schema(description = "New status of the message", example = "EDITED", allowableValues = {"SENT", "EDITED", "DELETED"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Message status is required")
        String status
) {
}
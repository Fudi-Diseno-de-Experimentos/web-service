package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Solicitud para abrir (o recuperar) una conversación directa 1 a 1.
 * El usuario que la inicia se deriva del token JWT, nunca del cuerpo.
 */
@Schema(description = "Request to open a direct conversation with another company member")
public record CreateConversationResource(
        @Schema(description = "User ID of the other participant", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Target user ID is required")
        UUID targetUserId
) {
}

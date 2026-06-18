package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Conversación directa (1 a 1) vista desde el usuario autenticado.
 * {@code otherUserId} es siempre el otro participante.
 */
@Schema(description = "Direct conversation information")
public record ConversationResource(
        @Schema(description = "Conversation identifier (also the group id used for messages)")
        UUID id,

        @Schema(description = "User ID of the other participant")
        UUID otherUserId,

        @Schema(description = "All participant user IDs (exactly 2)")
        List<UUID> memberIds,

        @Schema(description = "Date when the conversation was created")
        Date createdAt,

        @Schema(description = "Date when the conversation was last updated")
        Date updatedAt
) {
}

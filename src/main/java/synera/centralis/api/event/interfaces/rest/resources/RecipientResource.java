package synera.centralis.api.event.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import synera.centralis.api.event.domain.model.valueobjects.RecipientStatus;

import java.util.UUID;

/**
 * A single event recipient with their invitation response status.
 */
@Schema(description = "Event recipient and their invitation status")
public record RecipientResource(
        @Schema(description = "Recipient user ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "Invitation response status", example = "PENDING")
        RecipientStatus status
) {
}

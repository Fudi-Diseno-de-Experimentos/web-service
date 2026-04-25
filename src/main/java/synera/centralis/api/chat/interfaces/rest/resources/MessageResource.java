package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.UUID;

/**
 * Resource representing a message response.
 * Used for returning message information to clients.
 */
@Schema(description = "Message information")
public record MessageResource(
        @Schema(description = "Unique identifier of the message", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID messageId,
        
        @Schema(description = "Group ID the message belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID groupId,
        
        @Schema(description = "User ID who sent the message", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID senderId,
        
        @Schema(description = "Content of the message", example = "Hello everyone!")
        String body,
        
        @Schema(description = "Status of the message", example = "SENT", allowableValues = {"SENT", "EDITED", "DELETED"})
        String status,
        
        @Schema(description = "Date when the message was sent")
        Date sentAt,
        
        @Schema(description = "Date when the message was last edited")
        Date editedAt,
        
        @Schema(description = "Whether the message has been edited", example = "false")
        boolean isEdited,
        
        @Schema(description = "Whether the message is visible (not deleted)", example = "true")
        boolean isVisible
) {
}
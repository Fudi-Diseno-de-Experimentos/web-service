package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.UpdateMessageStatusCommand;
import synera.centralis.api.chat.interfaces.rest.resources.UpdateMessageStatusResource;
import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;

import java.util.UUID;

/**
 * Assembler to transform UpdateMessageStatusResource to UpdateMessageStatusCommand.
 * Handles the status changes of messages (SENT/EDITED/DELETED).
 */
public class UpdateMessageStatusCommandFromResourceAssembler {

    /**
     * Transforms an UpdateMessageStatusResource to an UpdateMessageStatusCommand.
     * @param messageId the ID of the message to update
     * @param resource the resource containing status data
     * @return the corresponding domain command
     */
    public static UpdateMessageStatusCommand toCommandFromResource(UUID messageId, UpdateMessageStatusResource resource) {
        return new UpdateMessageStatusCommand(
                messageId,
                MessageStatus.valueOf(resource.status())
        );
    }
}
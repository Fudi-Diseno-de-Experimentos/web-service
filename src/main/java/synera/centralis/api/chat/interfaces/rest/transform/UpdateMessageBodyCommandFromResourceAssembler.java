package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.UpdateMessageBodyCommand;
import synera.centralis.api.chat.interfaces.rest.resources.UpdateMessageBodyResource;

import java.util.UUID;

/**
 * Assembler to transform UpdateMessageBodyResource to UpdateMessageBodyCommand.
 * Handles the editing of message content.
 */
public class UpdateMessageBodyCommandFromResourceAssembler {

    /**
     * Transforms an UpdateMessageBodyResource to an UpdateMessageBodyCommand.
     * @param messageId the ID of the message to update
     * @param resource the resource containing updated body data
     * @return the corresponding domain command
     */
    public static UpdateMessageBodyCommand toCommandFromResource(UUID messageId, UpdateMessageBodyResource resource) {
        return new UpdateMessageBodyCommand(
                messageId,
                resource.body()
        );
    }
}
package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.CreateMessageCommand;
import synera.centralis.api.chat.interfaces.rest.resources.CreateMessageResource;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Assembler to transform CreateMessageResource to CreateMessageCommand.
 * Handles the creation of new messages in groups.
 */
public class CreateMessageCommandFromResourceAssembler {

    /**
     * Transforms a CreateMessageResource to a CreateMessageCommand.
     * @param groupId the ID of the group to create message in
     * @param resource the resource containing message data
     * @return the corresponding domain command
     */
    public static CreateMessageCommand toCommandFromResource(UUID groupId, CreateMessageResource resource) {
        return new CreateMessageCommand(
                groupId,
                new UserId(resource.senderId()),
                resource.body()
        );
    }
}
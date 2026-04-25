package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.CreateChatImageCommand;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.interfaces.rest.resources.CreateChatImageResource;

import java.util.UUID;

/**
 * Assembler class to transform CreateChatImageResource to CreateChatImageCommand.
 */
public class CreateChatImageCommandFromResourceAssembler {

    /**
     * Transforms a CreateChatImageResource to a CreateChatImageCommand.
     * @param groupId the group ID where the image will be shared
     * @param resource the resource to transform
     * @return the command
     */
    public static CreateChatImageCommand toCommandFromResource(UUID groupId, CreateChatImageResource resource) {
        return new CreateChatImageCommand(
            groupId,
            new UserId(resource.senderId()),
            resource.imageUrl()
        );
    }
}
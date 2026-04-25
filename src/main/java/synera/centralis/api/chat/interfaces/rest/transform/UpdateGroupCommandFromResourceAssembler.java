package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.UpdateGroupCommand;
import synera.centralis.api.chat.interfaces.rest.resources.UpdateGroupResource;

import java.util.UUID;

/**
 * Assembler to transform UpdateGroupResource to UpdateGroupCommand.
 * Handles the consolidated update command for group information.
 */
public class UpdateGroupCommandFromResourceAssembler {

    /**
     * Transforms an UpdateGroupResource to an UpdateGroupCommand.
     * @param groupId the ID of the group to update
     * @param resource the resource containing update data
     * @return the corresponding domain command
     */
    public static UpdateGroupCommand toCommandFromResource(UUID groupId, UpdateGroupResource resource) {
        return new UpdateGroupCommand(
                groupId,
                resource.name(),
                resource.description(),
                resource.imageUrl()
        );
    }
}
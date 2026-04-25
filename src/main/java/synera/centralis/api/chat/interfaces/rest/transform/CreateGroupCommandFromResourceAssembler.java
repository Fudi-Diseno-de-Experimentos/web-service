package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.CreateGroupCommand;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.interfaces.rest.resources.CreateGroupResource;

/**
 * Assembler to transform CreateGroupResource to CreateGroupCommand.
 * Follows the assembler pattern for clean separation between API and domain layers.
 */
public class CreateGroupCommandFromResourceAssembler {

    /**
     * Transforms a CreateGroupResource to a CreateGroupCommand.
     * @param resource the resource containing group creation data
     * @return the corresponding domain command
     */
    public static CreateGroupCommand toCommandFromResource(CreateGroupResource resource) {
        return new CreateGroupCommand(
                resource.name(),
                resource.description(),
                resource.imageUrl(),
                GroupVisibility.valueOf(resource.visibility()),
                resource.memberIds(),
                new UserId(resource.createdBy())
        );
    }
}
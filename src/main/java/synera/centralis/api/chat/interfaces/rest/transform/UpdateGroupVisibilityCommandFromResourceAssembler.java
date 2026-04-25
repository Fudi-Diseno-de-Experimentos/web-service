package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.UpdateGroupVisibilityCommand;
import synera.centralis.api.chat.interfaces.rest.resources.UpdateGroupVisibilityResource;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;

import java.util.UUID;

/**
 * Assembler to transform UpdateGroupVisibilityResource to UpdateGroupVisibilityCommand.
 * Handles visibility updates for groups.
 */
public class UpdateGroupVisibilityCommandFromResourceAssembler {

    /**
     * Transforms an UpdateGroupVisibilityResource to an UpdateGroupVisibilityCommand.
     * @param groupId the ID of the group to update
     * @param resource the resource containing visibility data
     * @return the corresponding domain command
     */
    public static UpdateGroupVisibilityCommand toCommandFromResource(UUID groupId, UpdateGroupVisibilityResource resource) {
        return new UpdateGroupVisibilityCommand(
                groupId,
                GroupVisibility.valueOf(resource.visibility())
        );
    }
}
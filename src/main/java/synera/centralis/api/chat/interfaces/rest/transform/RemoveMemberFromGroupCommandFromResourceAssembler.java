package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.RemoveMemberFromGroupCommand;
import synera.centralis.api.chat.interfaces.rest.resources.RemoveMemberFromGroupResource;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Assembler to transform RemoveMemberFromGroupResource to RemoveMemberFromGroupCommand.
 * Handles the removal of members from groups.
 */
public class RemoveMemberFromGroupCommandFromResourceAssembler {

    /**
     * Transforms a RemoveMemberFromGroupResource to a RemoveMemberFromGroupCommand.
     * @param groupId the ID of the group to remove member from
     * @param resource the resource containing member data
     * @return the corresponding domain command
     */
    public static RemoveMemberFromGroupCommand toCommandFromResource(UUID groupId, RemoveMemberFromGroupResource resource) {
        return new RemoveMemberFromGroupCommand(
                groupId,
                new UserId(resource.userId())
        );
    }
}
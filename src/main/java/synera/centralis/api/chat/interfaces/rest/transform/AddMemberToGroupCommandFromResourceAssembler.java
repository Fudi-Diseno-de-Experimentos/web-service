package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.AddMemberToGroupCommand;
import synera.centralis.api.chat.interfaces.rest.resources.AddMemberToGroupResource;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Assembler to transform AddMemberToGroupResource to AddMemberToGroupCommand.
 * Handles the addition of members to groups.
 */
public class AddMemberToGroupCommandFromResourceAssembler {

    /**
     * Transforms an AddMemberToGroupResource to an AddMemberToGroupCommand.
     * @param groupId the ID of the group to add member to
     * @param resource the resource containing member data
     * @return the corresponding domain command
     */
    public static AddMemberToGroupCommand toCommandFromResource(UUID groupId, AddMemberToGroupResource resource) {
        return new AddMemberToGroupCommand(
                groupId,
                new UserId(resource.userId())
        );
    }
}
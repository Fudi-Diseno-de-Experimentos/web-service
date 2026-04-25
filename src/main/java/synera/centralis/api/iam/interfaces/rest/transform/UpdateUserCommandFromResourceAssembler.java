package synera.centralis.api.iam.interfaces.rest.transform;

import java.util.UUID;

import synera.centralis.api.iam.domain.model.commands.UpdateUserCommand;
import synera.centralis.api.iam.interfaces.rest.resources.UpdateUserResource;

/**
 * Assembler to convert update user resource to update user command.
 * Handles password updates only - profile data is managed by Profile context.
 */
public class UpdateUserCommandFromResourceAssembler {

    /**
     * Converts a update user resource to a update user command.
     * @param userId the user id.
     * @param resource the {@link UpdateUserResource} resource.
     * @return the {@link UpdateUserCommand} command.
     */
    public static UpdateUserCommand toCommandFromResource(UUID userId, UpdateUserResource resource) {
        return new UpdateUserCommand(
                userId,
                resource.newPassword()
        );
    }
}

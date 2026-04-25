package synera.centralis.api.profile.interfaces.rest.transform;

import java.util.UUID;

import synera.centralis.api.profile.domain.model.commands.UpdateProfileCommand;
import synera.centralis.api.profile.interfaces.rest.resources.UpdateProfileResource;

/**
 * Assembler to convert update profile resource to update profile command.
 */
public class UpdateProfileCommandFromResourceAssembler {

    /**
     * Converts an update profile resource to an update profile command.
     * @param profileId the profile ID.
     * @param resource the {@link UpdateProfileResource} resource.
     * @return the {@link UpdateProfileCommand} command.
     */
    public static UpdateProfileCommand toCommandFromResource(UUID profileId, UpdateProfileResource resource) {
        return new UpdateProfileCommand(
                profileId,
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.avatarUrl(),
                resource.position(),
                resource.department()
        );
    }
}
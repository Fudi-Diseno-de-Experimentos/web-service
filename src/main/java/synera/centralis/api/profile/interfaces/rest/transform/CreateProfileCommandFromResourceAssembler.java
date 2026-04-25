package synera.centralis.api.profile.interfaces.rest.transform;

import synera.centralis.api.profile.domain.model.commands.CreateProfileCommand;
import synera.centralis.api.profile.interfaces.rest.resources.CreateProfileResource;

/**
 * Assembler to convert create profile resource to create profile command.
 */
public class CreateProfileCommandFromResourceAssembler {

    /**
     * Converts a create profile resource to a create profile command.
     * @param resource the {@link CreateProfileResource} resource.
     * @return the {@link CreateProfileCommand} command.
     */
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.userId(),
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.avatarUrl(),
                resource.position(),
                resource.department()
        );
    }
}
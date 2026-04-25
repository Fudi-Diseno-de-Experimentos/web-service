package synera.centralis.api.iam.interfaces.rest.transform;

import synera.centralis.api.iam.domain.model.commands.SignInCommand;
import synera.centralis.api.iam.interfaces.rest.resources.SignInResource;
public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(signInResource.username(), signInResource.password());
    }
}

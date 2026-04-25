package synera.centralis.api.iam.interfaces.rest.transform;

import synera.centralis.api.iam.domain.model.commands.SignUpCommand;
import synera.centralis.api.iam.domain.model.entities.Role;
import synera.centralis.api.iam.interfaces.rest.resources.SignUpResource;
import java.util.ArrayList;
import java.util.List;

/**
 * Assembler to convert SignUpResource to SignUpCommand
 */
public class SignUpCommandFromResourceAssembler {
    
    /**
     * Convert SignUpResource to SignUpCommand
     * @param resource the sign-up resource
     * @return the sign-up command
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        List<Role> roles = resource.roles() != null 
            ? resource.roles().stream().map(Role::toRoleFromName).toList() 
            : new ArrayList<>();
            
        return new SignUpCommand(
            resource.username(), 
            resource.password(),
            resource.name(),        // maps to firstName
            resource.lastname(),    // maps to lastName
            resource.email(),
            roles
        );
    }
}

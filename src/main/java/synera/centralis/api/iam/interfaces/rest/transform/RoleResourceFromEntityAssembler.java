package synera.centralis.api.iam.interfaces.rest.transform;

import synera.centralis.api.iam.domain.model.entities.Role;
import synera.centralis.api.iam.interfaces.rest.resources.RoleResource;
public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}

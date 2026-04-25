package synera.centralis.api.iam.interfaces.rest.transform;
/*
import quri.teelab.api.teelab.iam.domain.model.aggregates.User;
import quri.teelab.api.teelab.iam.interfaces.rest.resources.AuthenticatedUserResource;*/

import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(user.getId().toString(), user.getUsername(), token);
    }
}

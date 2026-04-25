package synera.centralis.api.profile.interfaces.rest.transform;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.interfaces.rest.resources.ProfileResource;

/**
 * Assembler to convert profile entity to profile resource.
 */
public class ProfileResourceFromEntityAssembler {

    /**
     * Converts a profile entity to a profile resource.
     * @param entity the {@link Profile} entity.
     * @return the {@link ProfileResource} resource.
     */
    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(
                entity.getId(),
                entity.getUserId().userId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getFullName(),
                entity.getAvatarUrl(),
                entity.getPosition(),
                entity.getDepartment()
        );
    }
}
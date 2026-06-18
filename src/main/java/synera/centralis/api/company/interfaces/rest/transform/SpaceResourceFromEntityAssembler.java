package synera.centralis.api.company.interfaces.rest.transform;

import synera.centralis.api.company.domain.model.aggregates.Space;
import synera.centralis.api.company.interfaces.rest.resources.SpaceResource;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class SpaceResourceFromEntityAssembler {

    public static SpaceResource toResourceFromEntity(Space entity) {
        return toResourceFromEntity(entity, null);
    }

    public static SpaceResource toResourceFromEntity(Space entity, Boolean available) {
        return new SpaceResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCompanyId() != null ? entity.getCompanyId().companyId() : null,
                available,
                LocalDateTime.ofInstant(entity.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(entity.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
    }
}

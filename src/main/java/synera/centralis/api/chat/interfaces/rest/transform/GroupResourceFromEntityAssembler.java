package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.interfaces.rest.resources.GroupResource;

import java.util.List;
import java.util.UUID;

/**
 * Assembler to transform Group entity to GroupResource.
 * Handles the transformation of domain entities to REST resources.
 */
public class GroupResourceFromEntityAssembler {

    /**
     * Transforms a Group entity to a GroupResource.
     * @param entity the domain entity
     * @return the corresponding REST resource
     */
    public static GroupResource toResourceFromEntity(Group entity) {
        // Convert Set<UserId> to List<UUID>
        List<UUID> memberUUIDs = entity.getMembers().stream()
                .map(userId -> userId.userId())
                .toList();
        
        return new GroupResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getVisibility().name(), // Convert enum to string
                memberUUIDs,
                entity.getMembers().size(),
                entity.getCreatedBy().userId(), // Convert UserId to UUID
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
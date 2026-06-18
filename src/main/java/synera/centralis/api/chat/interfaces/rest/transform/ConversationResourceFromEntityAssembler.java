package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.interfaces.rest.resources.ConversationResource;

import java.util.List;
import java.util.UUID;

/**
 * Transforma un grupo de tipo DIRECT en una {@link ConversationResource}
 * relativa al usuario autenticado.
 */
public class ConversationResourceFromEntityAssembler {

    /**
     * @param entity        la conversación directa (grupo tipo DIRECT)
     * @param currentUserId el usuario autenticado, para resolver "el otro"
     */
    public static ConversationResource toResourceFromEntity(Group entity, UUID currentUserId) {
        List<UUID> memberUUIDs = entity.getMembers().stream()
                .map(userId -> userId.userId())
                .toList();

        UUID otherUserId = memberUUIDs.stream()
                .filter(id -> !id.equals(currentUserId))
                .findFirst()
                .orElse(currentUserId);

        return new ConversationResource(
                entity.getId(),
                otherUserId,
                memberUUIDs,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.entities.ChatImage;
import synera.centralis.api.chat.interfaces.rest.resources.ChatImageResource;

/**
 * Assembler class to transform ChatImage entity to ChatImageResource.
 */
public class ChatImageResourceFromEntityAssembler {

    /**
     * Transforms a ChatImage entity to a ChatImageResource.
     * @param entity the entity to transform
     * @return the resource
     */
    public static ChatImageResource toResourceFromEntity(ChatImage entity) {
        return new ChatImageResource(
            entity.getImageId(),
            entity.getGroupId(),
            entity.getSenderId().userId(),
            entity.getImageUrl(),
            entity.getSentAt(),
            entity.isVisible()
        );
    }
}
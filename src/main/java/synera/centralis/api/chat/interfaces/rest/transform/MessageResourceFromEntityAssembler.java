package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.interfaces.rest.resources.MessageResource;
import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;

/**
 * Assembler to transform Message entity to MessageResource.
 * Handles the transformation of domain entities to REST resources.
 */
public class MessageResourceFromEntityAssembler {

    /**
     * Transforms a Message entity to a MessageResource.
     * @param entity the domain entity
     * @return the corresponding REST resource
     */
    public static MessageResource toResourceFromEntity(Message entity) {
        return new MessageResource(
                entity.getMessageId(),
                entity.getGroupId(),
                entity.getSenderId().userId(), // Convert UserId to UUID
                entity.getBody(),
                entity.getStatus().name(), // Convert MessageStatus to String
                entity.getSentAt(),
                entity.getEditedAt(),
                entity.getEditedAt() != null, // isEdited
                entity.getStatus() != MessageStatus.DELETED // isVisible
        );
    }
}
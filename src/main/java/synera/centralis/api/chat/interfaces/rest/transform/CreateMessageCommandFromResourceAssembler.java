package synera.centralis.api.chat.interfaces.rest.transform;

import synera.centralis.api.chat.domain.model.commands.CreateMessageCommand;
import synera.centralis.api.chat.interfaces.rest.resources.CreateMessageResource;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Assembler to transform CreateMessageResource to CreateMessageCommand.
 * Handles the creation of new messages in groups.
 */
public class CreateMessageCommandFromResourceAssembler {

    /**
     * Transforms a CreateMessageResource to a CreateMessageCommand.
     * @param groupId the ID of the group to create message in
     * @param resource the resource containing message data
     * @return the corresponding domain command
     */
    public static CreateMessageCommand toCommandFromResource(UUID groupId, CreateMessageResource resource) {
        return new CreateMessageCommand(
                groupId,
                new UserId(resource.senderId()),
                resource.body()
        );
    }

    /**
     * Variante segura: el remitente lo fija el controlador a partir del JWT,
     * no del cuerpo de la petición (evita suplantación).
     * @param groupId  grupo destino
     * @param senderId remitente autenticado (derivado del token)
     * @param resource recurso con el cuerpo del mensaje
     * @return el comando de dominio
     */
    public static CreateMessageCommand toCommandFromResource(UUID groupId, UUID senderId, CreateMessageResource resource) {
        return new CreateMessageCommand(
                groupId,
                new UserId(senderId),
                resource.body()
        );
    }
}
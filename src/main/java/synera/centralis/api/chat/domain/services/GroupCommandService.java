package synera.centralis.api.chat.domain.services;

import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain service interface for handling group-related commands.
 * Defines the contract for all group command operations.
 */
public interface GroupCommandService {

    /**
     * Handles the creation of a new group.
     * @param command the create group command
     * @return the created group
     */
    Group handle(CreateGroupCommand command);

    /**
     * Obtiene o crea la conversación directa (1 a 1) entre dos usuarios de la
     * misma compañía. Idempotente: reutiliza la existente si la hay.
     * @param command el comando de conversación directa
     * @return la conversación directa (nueva o existente)
     */
    Group handle(CreateDirectConversationCommand command);

    /**
     * Handles updating group information (name, description, image).
     * @param command the update group command
     * @return the updated group
     */
    Group handle(UpdateGroupCommand command);

    /**
     * Handles updating group visibility.
     * @param command the update group visibility command
     * @return the updated group
     */
    Group handle(UpdateGroupVisibilityCommand command);

    /**
     * Handles adding a member to a group.
     * @param command the add member command
     * @return the updated group
     */
    Group handle(AddMemberToGroupCommand command);

    /**
     * Handles removing a member from a group.
     * @param command the remove member command
     * @return the updated group
     */
    Group handle(RemoveMemberFromGroupCommand command);

    /**
     * Handles deleting a group.
     * @param command the delete group command
     */
    boolean handle(DeleteGroupCommand command);
}
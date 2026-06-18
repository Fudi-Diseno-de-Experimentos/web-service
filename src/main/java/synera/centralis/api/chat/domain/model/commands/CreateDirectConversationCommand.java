package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Comando para obtener-o-crear una conversación directa (1 a 1) entre dos
 * usuarios de la misma compañía. Es idempotente: si ya existe la conversación
 * entre ambos en esa compañía, se reutiliza.
 */
public record CreateDirectConversationCommand(
        UserId requester,
        UserId target,
        CompanyId companyId
) {
    public CreateDirectConversationCommand {
        if (requester == null) {
            throw new IllegalArgumentException("Requester cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target user cannot be null");
        }
        if (requester.equals(target)) {
            throw new IllegalArgumentException("Cannot start a direct conversation with yourself");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}

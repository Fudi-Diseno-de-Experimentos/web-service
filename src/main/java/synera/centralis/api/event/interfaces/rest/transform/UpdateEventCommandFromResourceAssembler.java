package synera.centralis.api.event.interfaces.rest.transform;

import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.interfaces.rest.resources.UpdateEventResource;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

/**
 * Assembler to convert UpdateEventResource to UpdateEventCommand.
 */
public class UpdateEventCommandFromResourceAssembler {

    public static UpdateEventCommand toCommandFromResource(UUID eventId, UpdateEventResource resource, CompanyId companyId) {
        return new UpdateEventCommand(
                eventId,
                resource.title(),
                resource.description(),
                resource.date(),
                resource.spaceId(),
                resource.recipientIds(),
                companyId
        );
    }
}


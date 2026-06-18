package synera.centralis.api.event.interfaces.rest.transform;

import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.event.interfaces.rest.resources.CreateEventResource;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Assembler to convert CreateEventResource to CreateEventCommand.
 */
public class CreateEventCommandFromResourceAssembler {

    public static CreateEventCommand toCommandFromResource(CreateEventResource resource, CompanyId companyId) {
        return new CreateEventCommand(
                resource.title(),
                resource.description(),
                resource.date(),
                resource.spaceId(),
                resource.recipientIds(),
                new UserId(resource.createdBy()),
                companyId
        );
    }
}
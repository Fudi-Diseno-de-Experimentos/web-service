package synera.centralis.api.company.interfaces.rest.transform;

import synera.centralis.api.company.domain.model.commands.CreateSpaceCommand;
import synera.centralis.api.company.interfaces.rest.resources.CreateSpaceResource;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

public class CreateSpaceCommandFromResourceAssembler {

    public static CreateSpaceCommand toCommandFromResource(CreateSpaceResource resource, CompanyId companyId) {
        return new CreateSpaceCommand(resource.name(), resource.description(), companyId);
    }
}

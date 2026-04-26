package synera.centralis.api.company.interfaces.rest.transform;

import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.interfaces.rest.resources.CreateCompanyResource;

public class CreateCompanyCommandFromResourceAssembler {
    public static CreateCompanyCommand toCommandFromResource(CreateCompanyResource resource) {
        return new CreateCompanyCommand(resource.ruc(), resource.nombre(), resource.iconUrl(), resource.isActive(), resource.userId());
    }
}

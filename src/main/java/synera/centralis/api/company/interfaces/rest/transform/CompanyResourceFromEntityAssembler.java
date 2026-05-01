package synera.centralis.api.company.interfaces.rest.transform;

import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.interfaces.rest.resources.CompanyResource;

public class CompanyResourceFromEntityAssembler {
    public static CompanyResource toResourceFromEntity(Company entity) {
        return new CompanyResource(entity.getId(), entity.getRuc(), entity.getNombre(), entity.getIconUrl(), entity.isActive(), entity.getUserId() != null ? entity.getUserId().userId() : null);
    }
}

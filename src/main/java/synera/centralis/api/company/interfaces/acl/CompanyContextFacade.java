package synera.centralis.api.company.interfaces.acl;

import org.springframework.stereotype.Service;
import synera.centralis.api.company.domain.model.queries.GetCompanyByJoinCodeQuery;
import synera.centralis.api.company.domain.services.CompanyQueryService;

import java.util.UUID;

@Service
public class CompanyContextFacade {

    private final CompanyQueryService companyQueryService;

    public CompanyContextFacade(CompanyQueryService companyQueryService) {
        this.companyQueryService = companyQueryService;
    }

    public UUID fetchCompanyIdByJoinCode(String joinCode) {
        var query = new GetCompanyByJoinCodeQuery(joinCode);
        var result = companyQueryService.handle(query);
        if (result.isEmpty()) return null;
        return result.get().getId();
    }
}

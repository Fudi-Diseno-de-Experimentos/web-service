package synera.centralis.api.company.domain.services;

import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.queries.GetAllCompaniesQuery;
import synera.centralis.api.company.domain.model.queries.GetCompanyByIdQuery;
import synera.centralis.api.company.domain.model.queries.GetCompanyByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface CompanyQueryService {
    Optional<Company> handle(GetCompanyByIdQuery query);
    List<Company> handle(GetAllCompaniesQuery query);
    Optional<Company> handle(GetCompanyByUserIdQuery query);
}

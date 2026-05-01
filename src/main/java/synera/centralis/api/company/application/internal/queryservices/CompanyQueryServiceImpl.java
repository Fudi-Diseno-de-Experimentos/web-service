package synera.centralis.api.company.application.internal.queryservices;

import org.springframework.stereotype.Service;
import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.queries.GetAllCompaniesQuery;
import synera.centralis.api.company.domain.model.queries.GetCompanyByIdQuery;
import synera.centralis.api.company.domain.model.queries.GetCompanyByUserIdQuery;
import synera.centralis.api.company.domain.services.CompanyQueryService;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.CompanyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyQueryServiceImpl implements CompanyQueryService {

    private final CompanyRepository companyRepository;

    public CompanyQueryServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Optional<Company> handle(GetCompanyByIdQuery query) {
        return companyRepository.findById(query.id());
    }

    @Override
    public List<Company> handle(GetAllCompaniesQuery query) {
        return companyRepository.findAll();
    }

    @Override
    public Optional<Company> handle(GetCompanyByUserIdQuery query) {
        return companyRepository.findByUserId(query.userId());
    }
}

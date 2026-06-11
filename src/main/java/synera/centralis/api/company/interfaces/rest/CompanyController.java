package synera.centralis.api.company.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.queries.GetAllCompaniesQuery;
import synera.centralis.api.company.domain.model.queries.GetCompanyByIdQuery;
import synera.centralis.api.company.domain.model.queries.GetCompanyByUserIdQuery;
import synera.centralis.api.company.domain.model.valueobjects.UserId;
import synera.centralis.api.company.domain.services.CompanyCommandService;
import synera.centralis.api.company.domain.services.CompanyQueryService;
import synera.centralis.api.company.interfaces.rest.resources.CompanyResource;
import synera.centralis.api.company.interfaces.rest.resources.CreateCompanyResource;
import synera.centralis.api.company.interfaces.rest.resources.UpdateCompanyResource;
import synera.centralis.api.company.interfaces.rest.transform.CompanyResourceFromEntityAssembler;
import synera.centralis.api.company.interfaces.rest.transform.CreateCompanyCommandFromResourceAssembler;
import synera.centralis.api.company.interfaces.rest.transform.UpdateCompanyCommandFromResourceAssembler;
import synera.centralis.api.iam.infrastructure.authorization.sfs.utils.SecurityUtils;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/companies", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Company", description = "Endpoints for managing companies")
public class CompanyController {

    private final CompanyCommandService companyCommandService;
    private final CompanyQueryService companyQueryService;

    public CompanyController(CompanyCommandService companyCommandService, CompanyQueryService companyQueryService) {
        this.companyCommandService = companyCommandService;
        this.companyQueryService = companyQueryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<CompanyResource> createCompany(@RequestBody CreateCompanyResource resource) {
        var createCompanyCommand = CreateCompanyCommandFromResourceAssembler.toCommandFromResource(resource);
        var company = companyCommandService.handle(createCompanyCommand);
        
        var companyResource = CompanyResourceFromEntityAssembler.toResourceFromEntity(company);
        return new ResponseEntity<>(companyResource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResource> getCompanyById(@PathVariable UUID id) {
        var getCompanyByIdQuery = new GetCompanyByIdQuery(id);
        var company = companyQueryService.handle(getCompanyByIdQuery);
        if (company.isEmpty()) return ResponseEntity.notFound().build();
        var companyResource = CompanyResourceFromEntityAssembler.toResourceFromEntity(company.get());
        return ResponseEntity.ok(companyResource);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CompanyResource> getCompanyByUserId(@PathVariable UUID userId) {
        var getCompanyByUserIdQuery = new GetCompanyByUserIdQuery(new UserId(userId));
        var company = companyQueryService.handle(getCompanyByUserIdQuery);
        if (company.isEmpty()) return ResponseEntity.notFound().build();
        var companyResource = CompanyResourceFromEntityAssembler.toResourceFromEntity(company.get());
        return ResponseEntity.ok(companyResource);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResource>> getAllCompanies() {
        var getAllCompaniesQuery = new GetAllCompaniesQuery();
        var companies = companyQueryService.handle(getAllCompaniesQuery);
        var companyResources = companies.stream()
                .map(CompanyResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(companyResources);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<CompanyResource> updateCompany(@PathVariable UUID id, @RequestBody UpdateCompanyResource resource) {
        verifyOwnership(id);
        var updateCompanyCommand = UpdateCompanyCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedCompany = companyCommandService.handle(updateCompanyCommand);
        
        var companyResource = CompanyResourceFromEntityAssembler.toResourceFromEntity(updatedCompany);
        return ResponseEntity.ok(companyResource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> deleteCompany(@PathVariable UUID id) {
        verifyOwnership(id);
        var deleteCompanyCommand = new DeleteCompanyCommand(id);
        companyCommandService.handle(deleteCompanyCommand);
        return ResponseEntity.ok("Company deleted successfully.");
    }

    /**
     * Ensures the authenticated user may only mutate their own company.
     * Prevents an admin/manager of one company from updating or deleting another.
     */
    private void verifyOwnership(UUID companyId) {
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (currentCompanyId == null || currentCompanyId.companyId() == null) {
            throw new UnauthorizedException("User is not associated with a company");
        }
        if (!currentCompanyId.companyId().equals(companyId)) {
            throw new UnauthorizedException("You are not allowed to manage another company");
        }
    }
}

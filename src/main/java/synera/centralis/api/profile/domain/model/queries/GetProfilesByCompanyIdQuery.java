package synera.centralis.api.profile.domain.model.queries;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

public record GetProfilesByCompanyIdQuery(CompanyId companyId) {
    public GetProfilesByCompanyIdQuery {
        if (companyId == null) {
            throw new IllegalArgumentException("CompanyId cannot be null");
        }
    }
}

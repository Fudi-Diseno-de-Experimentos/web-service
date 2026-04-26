package synera.centralis.api.shared.domain.events;

import java.util.UUID;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

public record UserAssignedToCompanyEvent(UUID userId, CompanyId companyId) {
    public static UserAssignedToCompanyEvent create(UUID userId, CompanyId companyId) {
        return new UserAssignedToCompanyEvent(userId, companyId);
    }
}

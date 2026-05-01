package synera.centralis.api.shared.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record CompanyId(UUID companyId) {
    public CompanyId {
        if (companyId == null) {
            throw new IllegalArgumentException("CompanyId cannot be null");
        }
    }
}

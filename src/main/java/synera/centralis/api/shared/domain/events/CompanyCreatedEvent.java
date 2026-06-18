package synera.centralis.api.shared.domain.events;

import java.util.UUID;

/**
 * Event published when a company is successfully created.
 */
public record CompanyCreatedEvent(UUID companyId, UUID userId) {
    public static CompanyCreatedEvent create(UUID companyId, UUID userId) {
        return new CompanyCreatedEvent(companyId, userId);
    }
}

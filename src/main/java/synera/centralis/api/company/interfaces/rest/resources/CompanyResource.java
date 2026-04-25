package synera.centralis.api.company.interfaces.rest.resources;

import java.util.UUID;

public record CompanyResource(UUID id, String ruc, String nombre, String iconUrl, boolean isActive, UUID userId) {
}

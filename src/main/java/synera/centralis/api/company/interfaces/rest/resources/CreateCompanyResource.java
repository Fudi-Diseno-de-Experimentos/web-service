package synera.centralis.api.company.interfaces.rest.resources;

import java.util.UUID;

public record CreateCompanyResource(String ruc, String nombre, String iconUrl, boolean isActive, UUID userId) {
}

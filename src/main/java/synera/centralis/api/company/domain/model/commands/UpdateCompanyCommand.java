package synera.centralis.api.company.domain.model.commands;

import java.util.UUID;

public record UpdateCompanyCommand(UUID id, String ruc, String nombre, String iconUrl, boolean isActive) {
}

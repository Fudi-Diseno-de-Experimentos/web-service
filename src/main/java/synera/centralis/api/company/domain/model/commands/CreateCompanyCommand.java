package synera.centralis.api.company.domain.model.commands;

import java.util.UUID;

public record CreateCompanyCommand(String ruc, String nombre, String iconUrl, boolean isActive, UUID userId) {
}

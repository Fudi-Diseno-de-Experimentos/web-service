package synera.centralis.api.company.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinCompanyResource(
        @NotBlank @Size(min = 6, max = 6) String joinCode
) {
}

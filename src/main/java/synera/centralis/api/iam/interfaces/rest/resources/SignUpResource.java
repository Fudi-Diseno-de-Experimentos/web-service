package synera.centralis.api.iam.interfaces.rest.resources;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Sign up resource
 */
@Schema(description = "Request to create a new user account")
public record SignUpResource(
    @Schema(description = "Username", example = "john.doe", required = true)
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    String username,
    
    @Schema(description = "Password", example = "password123", required = true)
    @NotBlank(message = "Password cannot be blank")
    @Size(max = 120, message = "Password must not exceed 120 characters")
    String password,
    
    @Schema(description = "First name", example = "John", required = true)
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,
    
    @Schema(description = "Last name", example = "Doe", required = true)
    @NotBlank(message = "Lastname cannot be blank")
    @Size(max = 100, message = "Lastname must not exceed 100 characters")
    String lastname,
    
    @Schema(description = "Email address", example = "john.doe@company.com", required = true)
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,
    
    @Schema(description = "User roles", example = "[\"ROLE_USER\"]")
    List<String> roles
) {
}

package synera.centralis.api.announcement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Create Comment Resource
 * DTO for creating new comments
 */
@Schema(description = "Data required to create a new comment")
public record CreateCommentResource(
    @Schema(description = "ID of the employee making the comment", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Employee ID is required")
    UUID employeeId,
    
    @Schema(description = "Content of the comment", example = "Gracias por la información, muy útil!")
    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment content cannot exceed 1000 characters")
    String content
) {}
package synera.centralis.api.announcement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update Announcement Resource
 * DTO for updating existing announcements
 */
@Schema(description = "Data required to update an existing announcement")
public record UpdateAnnouncementResource(
    @Schema(description = "Title of the announcement", example = "Nueva Política de Vacaciones")
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    String title,
    
    @Schema(description = "Detailed description of the announcement", example = "Se ha actualizado la política de vacaciones para incluir días adicionales...")
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    String description,
    
    @Schema(description = "Image URL associated with the announcement", example = "https://example.com/image.jpg", nullable = true)
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    String image,
    
    @Schema(description = "Priority level of the announcement", example = "HIGH", allowableValues = {"NORMAL", "HIGH", "URGENT"})
    @NotBlank(message = "Priority is required")
    String priority
) {}
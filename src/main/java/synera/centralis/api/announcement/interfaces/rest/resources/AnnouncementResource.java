package synera.centralis.api.announcement.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.UUID;

/**
 * Announcement Resource
 * DTO for Announcement responses
 */
@Schema(description = "Announcement information")
public record AnnouncementResource(
    @Schema(description = "Unique identifier of the announcement", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Title of the announcement", example = "Nueva Política de Vacaciones")
    String title,
    
    @Schema(description = "Detailed description of the announcement", example = "Se ha actualizado la política de vacaciones...")
    String description,
    
    @Schema(description = "Image URL associated with the announcement", example = "https://example.com/image.jpg", nullable = true)
    String image,
    
    @Schema(description = "Priority level of the announcement", example = "HIGH")
    String priority,
    
    @Schema(description = "ID of the user who created the announcement", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID createdBy,
    
    @Schema(description = "Creation timestamp", example = "2023-12-01T10:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    Date createdAt,
    
    @Schema(description = "Last update timestamp", example = "2023-12-01T10:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    Date updatedAt
) {}
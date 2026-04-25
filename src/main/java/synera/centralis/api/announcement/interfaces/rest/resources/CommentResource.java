package synera.centralis.api.announcement.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.UUID;

/**
 * Comment Resource
 * DTO for Comment responses
 */
@Schema(description = "Comment information")
public record CommentResource(
    @Schema(description = "Unique identifier of the comment", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "ID of the announcement this comment belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID announcementId,
    
    @Schema(description = "ID of the employee who made the comment", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID employeeId,
    
    @Schema(description = "Content of the comment", example = "Gracias por la información, muy útil!")
    String content,
    
    @Schema(description = "Creation timestamp", example = "2023-12-01T10:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    Date createdAt,
    
    @Schema(description = "Last update timestamp", example = "2023-12-01T10:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    Date updatedAt
) {}
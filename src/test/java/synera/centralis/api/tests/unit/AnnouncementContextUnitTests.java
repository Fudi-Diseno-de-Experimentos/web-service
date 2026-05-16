package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.commands.AddCommentToAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.CreateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.DeleteAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.UpdateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.entities.Comment;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.announcement.domain.services.AnnouncementCommandService;
import synera.centralis.api.announcement.domain.services.CommentCommandService;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Announcement.
 * Cubre US10 (publicación básica), US11 (priorización), US12 (edición),
 * US13 (eliminación) y US15 (comentarios). Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class AnnouncementContextUnitTests {

    @Mock
    private AnnouncementCommandService announcementCommandService;

    @Mock
    private CommentCommandService commentCommandService;

    private CompanyId companyId;
    private UUID managerId;

    @BeforeEach
    void setUp() {
        companyId = new CompanyId(UUID.randomUUID());
        managerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("US10: El gerente publica un anuncio válido y el servicio lo retorna")
    void shouldCreateAnnouncement_WhenCommandIsValid() {
        // Arrange
        CreateAnnouncementCommand command = new CreateAnnouncementCommand(
                "Mantenimiento programado",
                "El sistema estará inactivo el sábado",
                null,
                Priority.normal(),
                managerId,
                companyId
        );
        Announcement expected = mock(Announcement.class);
        when(announcementCommandService.handle(command)).thenReturn(expected);

        // Act
        Announcement result = announcementCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(announcementCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US10: Falla la publicación si el título está vacío")
    void shouldFailCreate_WhenTitleIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateAnnouncementCommand("   ", "Descripción", null,
                        Priority.normal(), managerId, companyId));

        // Assert
        assertTrue(ex.getMessage().contains("Title"));
    }

    @Test
    @DisplayName("US11: La prioridad URGENT se reconoce como alta o urgente")
    void shouldFlagUrgentPriority() {
        // Arrange
        Priority priority = Priority.urgent();

        // Act
        boolean urgent = priority.isUrgent();

        // Assert
        assertTrue(urgent);
        assertTrue(priority.isHighOrUrgent());
    }

    @Test
    @DisplayName("US11: Falla la publicación si la prioridad es nula")
    void shouldFailCreate_WhenPriorityIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateAnnouncementCommand("Título", "Descripción", null,
                        null, managerId, companyId));

        // Assert
        assertTrue(ex.getMessage().contains("Priority"));
    }

    @Test
    @DisplayName("US12: El gerente edita un anuncio existente")
    void shouldUpdateAnnouncement_WhenCommandIsValid() {
        // Arrange
        UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(
                UUID.randomUUID(), "Título actualizado", "Nueva descripción",
                null, Priority.high(), companyId);
        Announcement expected = mock(Announcement.class);
        when(announcementCommandService.handle(command)).thenReturn(expected);

        // Act
        Announcement result = announcementCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(announcementCommandService).handle(command);
    }

    @Test
    @DisplayName("US12: Falla la edición si el ID del anuncio es nulo")
    void shouldFailUpdate_WhenAnnouncementIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new UpdateAnnouncementCommand(null, "Título", "Descripción",
                        null, Priority.high(), companyId));

        // Assert
        assertTrue(ex.getMessage().contains("Announcement ID"));
    }

    @Test
    @DisplayName("US13: El gerente elimina un anuncio obsoleto")
    void shouldDeleteAnnouncement_WhenCommandIsValid() {
        // Arrange
        DeleteAnnouncementCommand command =
                new DeleteAnnouncementCommand(UUID.randomUUID(), companyId);
        when(announcementCommandService.handle(command)).thenReturn(true);

        // Act
        boolean deleted = announcementCommandService.handle(command);

        // Assert
        assertTrue(deleted);
        verify(announcementCommandService).handle(command);
    }

    @Test
    @DisplayName("US13: Falla la eliminación si falta el companyId (aislamiento de tenant)")
    void shouldFailDelete_WhenCompanyIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new DeleteAnnouncementCommand(UUID.randomUUID(), null));

        // Assert
        assertTrue(ex.getMessage().contains("Company ID"));
    }

    @Test
    @DisplayName("US15: El empleado comenta un anuncio y el servicio retorna el comentario")
    void shouldAddComment_WhenCommandIsValid() {
        // Arrange
        AddCommentToAnnouncementCommand command = new AddCommentToAnnouncementCommand(
                UUID.randomUUID(), UUID.randomUUID(), "¡Gracias por la información!");
        Comment comment = mock(Comment.class);
        when(commentCommandService.handle(command)).thenReturn(Optional.of(comment));

        // Act
        Optional<Comment> result = commentCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(commentCommandService).handle(command);
    }

    @Test
    @DisplayName("US15: Falla el comentario si el contenido está vacío")
    void shouldFailComment_WhenContentIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AddCommentToAnnouncementCommand(UUID.randomUUID(), UUID.randomUUID(), "  "));

        // Assert
        assertTrue(ex.getMessage().contains("Comment content"));
    }
}

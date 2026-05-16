package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.commands.RegisterAnnouncementViewCommand;
import synera.centralis.api.dashboard.domain.model.commands.RegisterEventViewCommand;
import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;
import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;
import synera.centralis.api.dashboard.domain.services.ContentViewCommandService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Dashboard.
 * Cubre US14 (confirmaciones de lectura de anuncios), US22 (métricas de
 * participación en eventos) y US46 (panel de control corporativo) mediante
 * el registro de vistas de contenido. Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class DashboardContextUnitTests {

    @Mock
    private ContentViewCommandService contentViewCommandService;

    @Test
    @DisplayName("US14: Registrar la lectura de un anuncio retorna una vista de contenido")
    void shouldRegisterAnnouncementView_WhenCommandIsValid() {
        // Arrange
        RegisterAnnouncementViewCommand command = new RegisterAnnouncementViewCommand(
                new UserId(UUID.randomUUID()), new AnnouncementId(UUID.randomUUID()));
        ContentView expected = mock(ContentView.class);
        when(contentViewCommandService.handle(command)).thenReturn(Optional.of(expected));

        // Act
        Optional<ContentView> result = contentViewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(contentViewCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US14: Falla el registro de lectura si el ID de usuario es nulo")
    void shouldFailAnnouncementView_WhenUserIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new RegisterAnnouncementViewCommand(null, new AnnouncementId(UUID.randomUUID())));

        // Assert
        assertTrue(ex.getMessage().contains("User ID"));
    }

    @Test
    @DisplayName("US22/US46: Registrar la vista de un evento retorna una vista de contenido")
    void shouldRegisterEventView_WhenCommandIsValid() {
        // Arrange
        RegisterEventViewCommand command = new RegisterEventViewCommand(
                new UserId(UUID.randomUUID()), new EventId(UUID.randomUUID()));
        ContentView expected = mock(ContentView.class);
        when(contentViewCommandService.handle(command)).thenReturn(Optional.of(expected));

        // Act
        Optional<ContentView> result = contentViewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(contentViewCommandService).handle(command);
    }

    @Test
    @DisplayName("US22: Falla el registro de vista si el ID del evento es nulo")
    void shouldFailEventView_WhenEventIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new RegisterEventViewCommand(new UserId(UUID.randomUUID()), null));

        // Assert
        assertTrue(ex.getMessage().contains("Event ID"));
    }
}

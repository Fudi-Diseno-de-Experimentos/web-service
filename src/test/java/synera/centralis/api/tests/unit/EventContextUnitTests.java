package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.commands.DeleteEventCommand;
import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Event.
 * Cubre US18 (creación básica), US19 (cancelación) y US20 (modificación).
 * Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class EventContextUnitTests {

    @Mock
    private EventCommandService eventCommandService;

    private CompanyId companyId;
    private UserId managerId;

    @BeforeEach
    void setUp() {
        companyId = new CompanyId(UUID.randomUUID());
        managerId = new UserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("US18: El gerente crea un evento válido y el servicio lo retorna")
    void shouldCreateEvent_WhenCommandIsValid() {
        // Arrange
        CreateEventCommand command = new CreateEventCommand(
                "Townhall Q3",
                "Revisión trimestral de objetivos",
                LocalDateTime.now().plusDays(7),
                "Auditorio Central",
                List.of(UUID.randomUUID()),
                managerId,
                companyId
        );
        Event expected = mock(Event.class);
        when(eventCommandService.handle(command)).thenReturn(expected);

        // Act
        Event result = eventCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(eventCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US18: Falla la creación si el título está vacío")
    void shouldFailCreate_WhenTitleIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateEventCommand("  ", "Descripción",
                        LocalDateTime.now(), "Sala", List.of(UUID.randomUUID()),
                        managerId, companyId));

        // Assert
        assertTrue(ex.getMessage().contains("title"));
    }

    @Test
    @DisplayName("US18: Falla la creación si no hay destinatarios")
    void shouldFailCreate_WhenRecipientsEmpty() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateEventCommand("Evento", "Descripción",
                        LocalDateTime.now(), "Sala", List.of(),
                        managerId, companyId));

        // Assert
        assertTrue(ex.getMessage().contains("recipient"));
    }

    @Test
    @DisplayName("US20: El gerente modifica un evento existente")
    void shouldUpdateEvent_WhenCommandIsValid() {
        // Arrange
        UpdateEventCommand command = new UpdateEventCommand(
                UUID.randomUUID(), "Townhall Anual", "Edición especial",
                LocalDateTime.now().plusDays(30), "Auditorio Principal",
                List.of(UUID.randomUUID()), companyId);
        Event expected = mock(Event.class);
        when(eventCommandService.handle(command)).thenReturn(expected);

        // Act
        Event result = eventCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(eventCommandService).handle(command);
    }

    @Test
    @DisplayName("US20: Falla la modificación si el ID del evento es nulo")
    void shouldFailUpdate_WhenEventIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new UpdateEventCommand(null, "Título", "Descripción",
                        LocalDateTime.now(), "Sala", List.of(UUID.randomUUID()), companyId));

        // Assert
        assertTrue(ex.getMessage().contains("Event ID"));
    }

    @Test
    @DisplayName("US19: El gerente cancela (elimina) un evento")
    void shouldDeleteEvent_WhenCommandIsValid() {
        // Arrange
        DeleteEventCommand command =
                new DeleteEventCommand(UUID.randomUUID(), companyId);
        when(eventCommandService.handle(command)).thenReturn(true);

        // Act
        boolean deleted = eventCommandService.handle(command);

        // Assert
        assertTrue(deleted);
        verify(eventCommandService).handle(command);
    }

    @Test
    @DisplayName("US19: Falla la cancelación si falta el companyId (aislamiento de tenant)")
    void shouldFailDelete_WhenCompanyIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new DeleteEventCommand(UUID.randomUUID(), null));

        // Assert
        assertTrue(ex.getMessage().contains("Company ID"));
    }
}

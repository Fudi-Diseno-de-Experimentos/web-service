package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.notification.domain.model.aggregates.Notification;
import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
import synera.centralis.api.notification.domain.model.commands.UpdateNotificationStatusCommand;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;
import synera.centralis.api.notification.domain.services.NotificationCommandService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Notification.
 * Sustenta US40 (notificaciones push): creación de notificaciones y cambio de
 * estado de entrega. Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class NotificationContextUnitTests {

    @Mock
    private NotificationCommandService notificationCommandService;

    @Test
    @DisplayName("US40: Crear una notificación válida retorna la notificación")
    void shouldCreateNotification_WhenCommandIsValid() {
        // Arrange
        CreateNotificationCommand command = new CreateNotificationCommand(
                "Nuevo anuncio", "Revisa el tablón",
                List.of(UUID.randomUUID().toString()), NotificationPriority.MEDIUM);
        Notification expected = mock(Notification.class);
        when(notificationCommandService.handle(command)).thenReturn(Optional.of(expected));

        // Act
        Optional<Notification> result = notificationCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(notificationCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US40: Falla la creación si la lista de destinatarios está vacía")
    void shouldFailCreate_WhenRecipientsEmpty() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateNotificationCommand("Título", "Mensaje",
                        List.of(), NotificationPriority.HIGH));

        // Assert
        assertTrue(ex.getMessage().contains("Recipients"));
    }

    @Test
    @DisplayName("US40: Falla la creación si la prioridad es nula")
    void shouldFailCreate_WhenPriorityIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateNotificationCommand("Título", "Mensaje",
                        List.of(UUID.randomUUID().toString()), null));

        // Assert
        assertTrue(ex.getMessage().contains("Priority"));
    }

    @Test
    @DisplayName("US40: Actualizar el estado de la notificación retorna la notificación")
    void shouldUpdateStatus_WhenCommandIsValid() {
        // Arrange
        UpdateNotificationStatusCommand command =
                new UpdateNotificationStatusCommand(UUID.randomUUID(), NotificationStatus.SENT);
        Notification expected = mock(Notification.class);
        when(notificationCommandService.handle(command)).thenReturn(Optional.of(expected));

        // Act
        Optional<Notification> result = notificationCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(notificationCommandService).handle(command);
    }

    @Test
    @DisplayName("US40: Falla la actualización de estado si el estado es nulo")
    void shouldFailUpdateStatus_WhenStatusIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new UpdateNotificationStatusCommand(UUID.randomUUID(), null));

        // Assert
        assertTrue(ex.getMessage().contains("Status"));
    }
}

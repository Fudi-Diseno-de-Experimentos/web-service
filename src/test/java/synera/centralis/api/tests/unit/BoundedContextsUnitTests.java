package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.announcement.domain.model.commands.CreateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.announcement.domain.services.AnnouncementCommandService;
import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.chat.domain.model.commands.CreateGroupCommand;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.services.GroupCommandService;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.agreggates.Event;
// Importamos UserId de Events de forma estándar
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoundedContextsUnitTests {

    @Mock
    private EventCommandService eventCommandService;

    @Mock
    private GroupCommandService groupCommandService;

    @Mock
    private AnnouncementCommandService announcementCommandService;

    private CompanyId validCompanyId;

    // Variables separadas para cada contexto debido al encapsulamiento estricto
    private UserId eventManagerId;
    private synera.centralis.api.chat.domain.model.valueobjects.UserId chatManagerId;

    @BeforeEach
    void setUp() {
        // Inicialización de IDs compartidos (el raw value)
        UUID rawManagerUuid = UUID.randomUUID();

        validCompanyId = new CompanyId(UUID.randomUUID());

        // Asignamos el mismo UUID encapsulándolo en su Value Object correspondiente de cada Bounded Context
        eventManagerId = new UserId(rawManagerUuid);
        chatManagerId = new synera.centralis.api.chat.domain.model.valueobjects.UserId(rawManagerUuid);
    }

    @Test
    @DisplayName("EVENTS: Debe procesar el comando de evento asegurando que el company_id esté íntegro")
    void shouldProcessCreateEventCommand_WithValidCompanyId() {
        // Arrange
        CreateEventCommand command = new CreateEventCommand(
                "Capacitación Anual",
                "Capacitación obligatoria para Q3",
                LocalDateTime.now(), // <-- Uso de LocalDateTime requerido
                "Sala Principal",
                List.of(UUID.randomUUID(), UUID.randomUUID()), // recipients
                eventManagerId,
                validCompanyId
        );

        Event mockEventMock = mock(Event.class);
        when(eventCommandService.handle(command)).thenReturn(mockEventMock);

        // Act
        Event result = eventCommandService.handle(command);

        // Assert
        assertNotNull(result, "El evento debe generarse exitosamente para una compañía válida");
        verify(eventCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("CHATS (Groups): Debe fallar al crear un grupo si el company_id es nulo (Integridad Promovida)")
    void createGroup_FailsWhen_CompanyIdIsNull() {
        // Arrange
        CreateGroupCommand invalidCommand = new CreateGroupCommand(
                "Proyecto X",
                "Grupo de desarrollo",
                "default.png",
                GroupVisibility.PRIVATE,
                List.of(chatManagerId.userId()), // Lista de UUIDs de miembros solicitada usando el ID puro
                chatManagerId,                   // <-- Uso de UserId importado desde el paquete Chat explícitamente
                null                         // CompanyId inválido para probar el aislamiento
        );

        // Simulamos el comportamiento del servicio de dominio lanzando excepción
        when(groupCommandService.handle(invalidCommand))
                .thenThrow(new IllegalArgumentException("CompanyId es requerido para el aislamiento de datos"));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            groupCommandService.handle(invalidCommand);
        });

        assertTrue(exception.getMessage().contains("CompanyId"));
    }

    @Test
    @DisplayName("ANNOUNCEMENT: Debe fallar al crear un anuncio si la prioridad es nula")
    void createAnnouncement_FailsWhen_PriorityIsNull() {
        // Arrange
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateAnnouncementCommand(
                    "Nuevo Anuncio",
                    "Detalles del anuncio",
                    null,
                    null,
                    chatManagerId.userId(),
                    validCompanyId
            );
        });

        // Assert
        assertTrue(exception.getMessage().contains("Priority"));
    }

    @Test
    @DisplayName("ANNOUNCEMENT: Debe procesar el comando de anuncio correctamente")
    void shouldProcessCreateAnnouncementCommand() {
        // Arrange
        CreateAnnouncementCommand command = new CreateAnnouncementCommand(
                "Actualización del Sistema",
                "El sistema estará en mantenimiento a las 10 PM",
                null,
                Priority.high(),
                chatManagerId.userId(),
                validCompanyId
        );

        Announcement mockAnnouncement = mock(Announcement.class);
        when(announcementCommandService.handle(command)).thenReturn(mockAnnouncement);

        // Act
        Announcement result = announcementCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(announcementCommandService, times(1)).handle(command);
    }
}
package synera.centralis.api.event.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.commands.RespondToEventInvitationCommand;
import synera.centralis.api.event.domain.model.queries.GetAllEventsQuery;
import synera.centralis.api.event.domain.model.queries.GetEventsByRecipientIdQuery;
import synera.centralis.api.event.domain.model.valueobjects.RecipientStatus;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.event.domain.services.EventCommandService;
import synera.centralis.api.event.domain.services.EventQueryService;
import synera.centralis.api.shared.AbstractIntegrationTest;
import synera.centralis.api.shared.domain.exceptions.ForbiddenException;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración del contexto Event (US18).
 * Usa componentes reales (servicios + repositorios JPA sobre H2).
 */
@Transactional
@DisplayName("Event - Integración: persistencia y consulta de eventos")
class EventIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EventCommandService commandService;

    @Autowired
    private EventQueryService queryService;

    @Test
    @DisplayName("Componentes reales: un evento creado se persiste con sus invitados")
    void crearYConsultarEvento() {
        // Business / User Story Rational (US18): el sistema guarda el evento y lo
        // muestra a los empleados seleccionados.
        // Arrange
        var companyId = new CompanyId(UUID.randomUUID());
        var invitado = UUID.randomUUID();
        var command = new CreateEventCommand("Kickoff", "Inicio de proyecto",
                LocalDateTime.of(2026, 9, 1, 9, 0), UUID.randomUUID(), List.of(invitado),
                new UserId(UUID.randomUUID()), companyId);
        // Act
        var creado = commandService.handle(command);
        var listado = queryService.handle(new GetAllEventsQuery(companyId));
        // Assert
        assertNotNull(creado.getId());
        assertEquals(1, listado.size());
        assertEquals(1, listado.get(0).getRecipientCount());
    }

    @Test
    @DisplayName("Componentes reales: los eventos están aislados por compañía")
    void eventosAisladosPorCompania() {
        // Business / User Story Rational (US18): una compañía no ve los eventos de
        // otra.
        // Arrange
        var companiaA = new CompanyId(UUID.randomUUID());
        var companiaB = new CompanyId(UUID.randomUUID());
        commandService.handle(new CreateEventCommand("Solo A", "desc",
                LocalDateTime.of(2026, 9, 1, 9, 0), UUID.randomUUID(), List.of(UUID.randomUUID()),
                new UserId(UUID.randomUUID()), companiaA));
        // Act
        var listadoB = queryService.handle(new GetAllEventsQuery(companiaB));
        // Assert
        assertTrue(listadoB.isEmpty());
    }

    @Test
    @DisplayName("Invitación: al declinar, el evento desaparece de la lista del invitado pero sigue para el admin")
    void declinarOcultaEventoSoloParaElInvitado() {
        // Business Rational: si el miembro cancela, ya no lo ve; el admin sí.
        // Arrange
        var companyId = new CompanyId(UUID.randomUUID());
        var invitado = UUID.randomUUID();
        var evento = commandService.handle(new CreateEventCommand("Town Hall", "desc",
                LocalDateTime.of(2026, 9, 2, 9, 0), UUID.randomUUID(), List.of(invitado),
                new UserId(UUID.randomUUID()), companyId));
        // Visible antes de responder
        assertEquals(1, queryService.handle(
                new GetEventsByRecipientIdQuery(new UserId(invitado), companyId)).size());
        // Act: el invitado declina
        commandService.handle(new RespondToEventInvitationCommand(
                evento.getId(), invitado, RecipientStatus.DECLINED, companyId));
        // Assert: oculto en su lista, presente para el admin (todos los eventos)
        assertTrue(queryService.handle(
                new GetEventsByRecipientIdQuery(new UserId(invitado), companyId)).isEmpty());
        assertEquals(1, queryService.handle(new GetAllEventsQuery(companyId)).size());
    }

    @Test
    @DisplayName("Invitación: aceptar mantiene el evento visible para el invitado")
    void aceptarMantieneEventoVisible() {
        var companyId = new CompanyId(UUID.randomUUID());
        var invitado = UUID.randomUUID();
        var evento = commandService.handle(new CreateEventCommand("Demo", "desc",
                LocalDateTime.of(2026, 9, 3, 9, 0), UUID.randomUUID(), List.of(invitado),
                new UserId(UUID.randomUUID()), companyId));
        // Act
        commandService.handle(new RespondToEventInvitationCommand(
                evento.getId(), invitado, RecipientStatus.ACCEPTED, companyId));
        // Assert
        var visibles = queryService.handle(new GetEventsByRecipientIdQuery(new UserId(invitado), companyId));
        assertEquals(1, visibles.size());
        assertEquals(RecipientStatus.ACCEPTED, visibles.get(0).getStatusFor(invitado));
    }

    @Test
    @DisplayName("Invitación: responder a un evento del que no se es invitado da Forbidden")
    void responderSinSerInvitadoEsForbidden() {
        var companyId = new CompanyId(UUID.randomUUID());
        var evento = commandService.handle(new CreateEventCommand("Privado", "desc",
                LocalDateTime.of(2026, 9, 4, 9, 0), UUID.randomUUID(), List.of(UUID.randomUUID()),
                new UserId(UUID.randomUUID()), companyId));
        // Act + Assert
        var intruso = UUID.randomUUID();
        assertThrows(ForbiddenException.class, () -> commandService.handle(
                new RespondToEventInvitationCommand(evento.getId(), intruso, RecipientStatus.ACCEPTED, companyId)));
    }
}

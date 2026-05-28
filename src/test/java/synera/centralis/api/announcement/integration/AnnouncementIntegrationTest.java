package synera.centralis.api.announcement.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.announcement.domain.model.commands.CreateAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.queries.GetAllAnnouncementsQuery;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;
import synera.centralis.api.announcement.domain.services.AnnouncementCommandService;
import synera.centralis.api.announcement.domain.services.AnnouncementQueryService;
import synera.centralis.api.shared.AbstractIntegrationTest;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración del contexto Announcement (US10-US11).
 * Usa componentes reales (servicios + repositorios JPA sobre H2). Firebase y el
 * servicio externo de notificaciones quedan mockeados vía {@link AbstractIntegrationTest}.
 */
@Transactional
@DisplayName("Announcement - Integración: persistencia y consulta de anuncios")
class AnnouncementIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AnnouncementCommandService commandService;

    @Autowired
    private AnnouncementQueryService queryService;

    @Test
    @DisplayName("Componentes reales: un anuncio publicado se persiste y aparece en el listado")
    void publicarYConsultarAnuncio() {
        // Business / User Story Rational (US10): el sistema guarda el anuncio y lo
        // muestra donde los empleados puedan verlo.
        // Arrange
        var companyId = new CompanyId(UUID.randomUUID());
        var command = new CreateAnnouncementCommand("Mantenimiento", "Habrá corte de luz", null,
                Priority.normal(), UUID.randomUUID(), companyId);
        // Act
        var creado = commandService.handle(command);
        var listado = queryService.handle(new GetAllAnnouncementsQuery(companyId));
        // Assert
        assertNotNull(creado.getId());
        assertEquals(1, listado.size());
        assertEquals("Mantenimiento", listado.get(0).getTitle());
    }

    @Test
    @DisplayName("Componentes reales: el listado está aislado por compañía (multi-tenant)")
    void listadoAisladoPorCompania() {
        // Business / User Story Rational (US10): cada empresa solo ve sus propios
        // anuncios, no los de otras compañías.
        // Arrange
        var companiaA = new CompanyId(UUID.randomUUID());
        var companiaB = new CompanyId(UUID.randomUUID());
        commandService.handle(new CreateAnnouncementCommand("De A", "contenido", null,
                Priority.urgent(), UUID.randomUUID(), companiaA));
        // Act
        var listadoB = queryService.handle(new GetAllAnnouncementsQuery(companiaB));
        // Assert
        assertTrue(listadoB.isEmpty());
    }
}

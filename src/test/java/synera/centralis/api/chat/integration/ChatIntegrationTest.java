package synera.centralis.api.chat.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.chat.domain.model.commands.CreateGroupCommand;
import synera.centralis.api.chat.domain.model.queries.GetGroupByIdQuery;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.domain.services.GroupCommandService;
import synera.centralis.api.chat.domain.services.GroupQueryService;
import synera.centralis.api.shared.AbstractIntegrationTest;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración del contexto Chat (US23).
 * Usa componentes reales (servicios + repositorios JPA sobre H2).
 */
@Transactional
@DisplayName("Chat - Integración: creación y consulta de grupos")
class ChatIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private GroupCommandService commandService;

    @Autowired
    private GroupQueryService queryService;

    @Test
    @DisplayName("Componentes reales: un grupo creado se persiste y se recupera por id")
    void crearYConsultarGrupo() {
        // Business / User Story Rational (US23): el sistema crea el chat con todos
        // los miembros añadidos y queda disponible para consulta.
        // Arrange
        var companyId = new CompanyId(UUID.randomUUID());
        var creador = new UserId(UUID.randomUUID());
        var command = new CreateGroupCommand("Proyecto X", "Coordinación", null,
                GroupVisibility.PUBLIC, List.of(UUID.randomUUID()), creador, companyId);
        // Act
        var creado = commandService.handle(command);
        var recuperado = queryService.handle(new GetGroupByIdQuery(creado.getId(), companyId));
        // Assert
        assertTrue(recuperado.isPresent());
        assertEquals("Proyecto X", recuperado.get().getName());
        assertTrue(recuperado.get().isMember(creador));
        assertEquals(2, recuperado.get().getMemberCount());
    }

    @Test
    @DisplayName("Componentes reales: un grupo de otra compañía no es accesible")
    void grupoAisladoPorCompania() {
        // Business / User Story Rational (US23): los grupos están acotados a la
        // compañía a la que pertenecen.
        // Arrange
        var companiaA = new CompanyId(UUID.randomUUID());
        var companiaB = new CompanyId(UUID.randomUUID());
        var creado = commandService.handle(new CreateGroupCommand("Solo A", null, null,
                GroupVisibility.PRIVATE, List.of(UUID.randomUUID()), new UserId(UUID.randomUUID()), companiaA));
        // Act
        var recuperado = queryService.handle(new GetGroupByIdQuery(creado.getId(), companiaB));
        // Assert
        assertTrue(recuperado.isEmpty());
    }
}

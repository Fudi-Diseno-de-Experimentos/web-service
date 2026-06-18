package synera.centralis.api.company.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.domain.model.queries.GetCompanyByIdQuery;
import synera.centralis.api.company.domain.services.CompanyCommandService;
import synera.centralis.api.company.domain.services.CompanyQueryService;
import synera.centralis.api.shared.AbstractIntegrationTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración del contexto Company (US41-US43).
 * Usa componentes reales (servicios + repositorios JPA sobre H2).
 */
@Transactional
@DisplayName("Company - Integración: registro, consulta y baja de compañías")
class CompanyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CompanyCommandService commandService;

    @Autowired
    private CompanyQueryService queryService;

    @Test
    @DisplayName("Componentes reales: registrar una compañía la persiste con código de unión")
    void registrarYConsultarCompania() {
        // Business / User Story Rational (US41): el sistema crea la Company al
        // registrarse con nombre legal, RUC y logo.
        // Arrange
        var command = new CreateCompanyCommand("20123456789", "Synera SAC", "http://logo/x.png", true, UUID.randomUUID());
        // Act
        var creada = commandService.handle(command);
        var recuperada = queryService.handle(new GetCompanyByIdQuery(creada.getId()));
        // Assert
        assertTrue(recuperada.isPresent());
        assertEquals("20123456789", recuperada.get().getRuc());
        assertEquals(6, recuperada.get().getJoinCode().length());
        assertTrue(recuperada.get().isActive());
    }

    @Test
    @DisplayName("Componentes reales: dar de baja una compañía la deja inactiva en la base")
    void darDeBajaCompania() {
        // Business / User Story Rational (US43): el gerente desactiva la cuenta y
        // el estado cambia a inactivo de forma persistente.
        // Arrange
        var creada = commandService.handle(
                new CreateCompanyCommand("20123456789", "Synera SAC", "http://logo/x.png", true, UUID.randomUUID()));
        // Act
        commandService.handle(new UpdateCompanyCommand(creada.getId(), "20123456789", "Synera SAC", "http://logo/x.png", false));
        var recuperada = queryService.handle(new GetCompanyByIdQuery(creada.getId()));
        // Assert
        assertTrue(recuperada.isPresent());
        assertFalse(recuperada.get().isActive());
    }
}

package synera.centralis.api.company.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.company.application.internal.commandservices.CompanyCommandServiceImpl;
import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.CompanyRepository;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del contexto Company (US41-US43).
 * Se prueba la orquestación de CompanyCommandServiceImpl con un repositorio
 * mockeado y las invariantes del agregado Company.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Company - Reglas de negocio de compañías (US41-US43)")
class CompanyUnitTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyCommandServiceImpl companyCommandService;

    private CreateCompanyCommand crearComando() {
        return new CreateCompanyCommand("20123456789", "Synera SAC", "http://logo/x.png", true, UUID.randomUUID());
    }

    @Test
    @DisplayName("Happy: registrar una compañía la persiste con sus datos")
    void registrarCompania() {
        // Business / User Story Rational (US41): el representante legal registra
        // su compañía con nombre legal, RUC y logo.
        // Arrange
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        var company = companyCommandService.handle(crearComando());
        // Assert
        assertEquals("20123456789", company.getRuc());
        assertEquals("Synera SAC", company.getNombre());
        assertTrue(company.isActive());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    @DisplayName("Integridad de datos: una compañía nueva recibe un código de unión de 6 caracteres")
    void companiaGeneraCodigoDeUnion() {
        // Business / User Story Rational (US44): el código de unión permite
        // vincular empleados a la compañía; debe generarse íntegro.
        // Act
        var company = new Company(crearComando());
        // Assert
        assertNotNull(company.getJoinCode());
        assertEquals(6, company.getJoinCode().length());
        assertTrue(company.getJoinCode().matches("[A-Z0-9]{6}"));
    }

    @Test
    @DisplayName("Condicional A: dar de baja una compañía existente la marca como inactiva")
    void darDeBajaCompaniaExistente() {
        // Business / User Story Rational (US43): el gerente desactiva la cuenta y
        // su estado cambia a inactivo.
        // Arrange
        var id = UUID.randomUUID();
        var existente = new Company(crearComando());
        when(companyRepository.findById(id)).thenReturn(Optional.of(existente));
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));
        var comando = new UpdateCompanyCommand(id, "20123456789", "Synera SAC", "http://logo/x.png", false);
        // Act
        var actualizada = companyCommandService.handle(comando);
        // Assert
        assertFalse(actualizada.isActive());
    }

    @Test
    @DisplayName("Excepción por estado inválido: actualizar una compañía inexistente falla")
    void actualizarCompaniaInexistenteFalla() {
        // Business / User Story Rational (US42): no se puede editar el perfil de
        // una compañía que no existe.
        // Arrange
        var id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());
        var comando = new UpdateCompanyCommand(id, "20123456789", "X", null, true);
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> companyCommandService.handle(comando));
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Happy: eliminar una compañía existente la remueve del repositorio")
    void eliminarCompaniaExistente() {
        // Business / User Story Rational (US43): al dar de baja, la compañía se
        // remueve de los recursos accesibles.
        // Arrange
        var id = UUID.randomUUID();
        when(companyRepository.existsById(id)).thenReturn(true);
        // Act
        var resultado = companyCommandService.handle(new DeleteCompanyCommand(id));
        // Assert
        assertTrue(resultado);
        verify(companyRepository).deleteById(id);
    }

    @Test
    @DisplayName("Excepción por estado inválido: eliminar una compañía inexistente falla")
    void eliminarCompaniaInexistenteFalla() {
        // Business / User Story Rational (US43): no se puede dar de baja una
        // compañía que no existe.
        // Arrange
        var id = UUID.randomUUID();
        when(companyRepository.existsById(id)).thenReturn(false);
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> companyCommandService.handle(new DeleteCompanyCommand(id)));
        verify(companyRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Integridad de datos: actualizar el agregado reemplaza todos sus campos básicos")
    void actualizarAgregadoReemplazaCampos() {
        // Business / User Story Rational (US42): el gerente actualiza dirección y
        // logo y los cambios se reflejan en el agregado.
        // Arrange
        var company = new Company(crearComando());
        // Act
        company.update("20999999999", "Synera Holdings", "http://logo/nuevo.png", true);
        // Assert
        assertEquals("20999999999", company.getRuc());
        assertEquals("Synera Holdings", company.getNombre());
        assertEquals("http://logo/nuevo.png", company.getIconUrl());
    }
}

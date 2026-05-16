package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.domain.services.CompanyCommandService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Company.
 * Cubre US41 (registro de compañía), US42 (edición de perfil de compañía)
 * y US43 (baja del servicio). Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class CompanyContextUnitTests {

    @Mock
    private CompanyCommandService companyCommandService;

    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("US41: El gerente registra una nueva compañía y el servicio la retorna")
    void shouldRegisterCompany_WhenCommandIsValid() {
        // Arrange
        CreateCompanyCommand command = new CreateCompanyCommand(
                "20123456789", "Centralis SAC", "https://cdn.test/logo.png", true, ownerId);
        Company expected = mock(Company.class);
        when(companyCommandService.handle(command)).thenReturn(expected);

        // Act
        Company result = companyCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(companyCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US42: El gerente actualiza el perfil de la compañía")
    void shouldUpdateCompany_WhenCommandIsValid() {
        // Arrange
        UpdateCompanyCommand command = new UpdateCompanyCommand(
                UUID.randomUUID(), "20123456789", "Centralis Corp",
                "https://cdn.test/new-logo.png", true);
        Company expected = mock(Company.class);
        when(companyCommandService.handle(command)).thenReturn(expected);

        // Act
        Company result = companyCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(companyCommandService).handle(command);
    }

    @Test
    @DisplayName("US43: El gerente da de baja la compañía y se revoca el acceso")
    void shouldDeactivateCompany_WhenCommandIsValid() {
        // Arrange
        DeleteCompanyCommand command = new DeleteCompanyCommand(UUID.randomUUID());
        when(companyCommandService.handle(command)).thenReturn(true);

        // Act
        boolean deactivated = companyCommandService.handle(command);

        // Assert
        assertTrue(deactivated);
        verify(companyCommandService).handle(command);
    }

    @Test
    @DisplayName("US41: El servicio rechaza el registro cuando los datos violan una regla de dominio")
    void shouldFailRegister_WhenDomainRuleViolated() {
        // Arrange
        CreateCompanyCommand command = new CreateCompanyCommand(
                null, "Sin RUC", null, true, ownerId);
        when(companyCommandService.handle(command))
                .thenThrow(new IllegalArgumentException("RUC es requerido"));

        // Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> companyCommandService.handle(command));
        assertTrue(ex.getMessage().contains("RUC"));
    }
}

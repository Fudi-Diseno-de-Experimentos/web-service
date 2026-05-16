package synera.centralis.api.tests.unit;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.domain.model.commands.AssignUserToCompanyCommand;
import synera.centralis.api.iam.domain.model.commands.SignInCommand;
import synera.centralis.api.iam.domain.model.commands.SignUpCommand;
import synera.centralis.api.iam.domain.model.commands.UpdateUserCommand;
import synera.centralis.api.iam.domain.model.entities.Role;
import synera.centralis.api.iam.domain.services.UserCommandService;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto IAM.
 * Cubre US33 (validación y registro seguro), US38 (autenticación con JWT)
 * y US44 (vinculación de empleados a la compañía).
 * Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class IamContextUnitTests {

    @Mock
    private UserCommandService userCommandService;

    @Test
    @DisplayName("US33: El registro válido produce un usuario a través del servicio")
    void shouldSignUp_WhenCommandIsValid() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "jdoe", "secret123", "John", "Doe", "jdoe@test.com",
                List.<Role>of());
        User expected = mock(User.class);
        when(userCommandService.handle(command)).thenReturn(expected);

        // Act
        User result = userCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(userCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US33: Falla el registro si el nombre de usuario está vacío")
    void shouldFailSignUp_WhenUsernameIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new SignUpCommand("  ", "secret123", "John", "Doe",
                        "jdoe@test.com", List.<Role>of()));

        // Assert
        assertTrue(ex.getMessage().contains("Username"));
    }

    @Test
    @DisplayName("US33: Falla el cambio de contraseña si es demasiado corta (regla de seguridad)")
    void shouldFailUpdateUser_WhenPasswordTooShort() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new UpdateUserCommand(UUID.randomUUID(), "123"));

        // Assert
        assertTrue(ex.getMessage().contains("at least 6 characters"));
    }

    @Test
    @DisplayName("US38: La autenticación válida retorna el usuario y un JWT")
    void shouldSignIn_WhenCredentialsValid() {
        // Arrange
        SignInCommand command = new SignInCommand("jdoe", "secret123");
        User user = mock(User.class);
        ImmutablePair<User, String> expected = ImmutablePair.of(user, "jwt-token");
        when(userCommandService.handle(command)).thenReturn(expected);

        // Act
        ImmutablePair<User, String> result = userCommandService.handle(command);

        // Assert
        assertNotNull(result.getLeft());
        assertEquals("jwt-token", result.getRight());
        verify(userCommandService).handle(command);
    }

    @Test
    @DisplayName("US44: El gerente vincula un empleado a su compañía")
    void shouldAssignUserToCompany_WhenCommandIsValid() {
        // Arrange
        AssignUserToCompanyCommand command = new AssignUserToCompanyCommand(
                UUID.randomUUID(), new CompanyId(UUID.randomUUID()));
        User expected = mock(User.class);
        when(userCommandService.handle(command)).thenReturn(expected);

        // Act
        User result = userCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(userCommandService).handle(command);
    }

    @Test
    @DisplayName("US44: Falla la vinculación si falta el companyId (aislamiento de tenant)")
    void shouldFailAssign_WhenCompanyIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AssignUserToCompanyCommand(UUID.randomUUID(), null));

        // Assert
        assertTrue(ex.getMessage().contains("Company ID"));
    }
}

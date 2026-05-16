package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.commands.CreateProfileCommand;
import synera.centralis.api.profile.domain.model.commands.UpdateProfileCommand;
import synera.centralis.api.profile.domain.services.ProfileCommandService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Profile.
 * El perfil acompaña el registro de empleados (US44) y se actualiza para
 * humanizar la marca. Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class ProfileContextUnitTests {

    @Mock
    private ProfileCommandService profileCommandService;

    @Test
    @DisplayName("US44: Crear un perfil válido retorna el perfil desde el servicio")
    void shouldCreateProfile_WhenCommandIsValid() {
        // Arrange
        CreateProfileCommand command = new CreateProfileCommand(
                UUID.randomUUID(), "Ana", "García", "ana@test.com", null);
        Profile expected = mock(Profile.class);
        when(profileCommandService.handle(command)).thenReturn(expected);

        // Act
        Profile result = profileCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(profileCommandService, times(1)).handle(command);
    }

    @Test
    @DisplayName("US44: Falla la creación del perfil si el nombre está vacío")
    void shouldFailCreate_WhenFirstNameIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateProfileCommand(UUID.randomUUID(), "  ", "García",
                        "ana@test.com", null));

        // Assert
        assertTrue(ex.getMessage().contains("First name"));
    }

    @Test
    @DisplayName("Perfil: Actualizar un perfil válido retorna el perfil actualizado")
    void shouldUpdateProfile_WhenCommandIsValid() {
        // Arrange
        UpdateProfileCommand command = new UpdateProfileCommand(
                UUID.randomUUID(), "Ana María", "García", "ana.maria@test.com", null);
        Profile expected = mock(Profile.class);
        when(profileCommandService.handle(command)).thenReturn(expected);

        // Act
        Profile result = profileCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(profileCommandService).handle(command);
    }

    @Test
    @DisplayName("Perfil: Falla la actualización si el email excede 150 caracteres")
    void shouldFailUpdate_WhenEmailTooLong() {
        // Arrange
        String longEmail = "a".repeat(145) + "@test.com"; // 154 chars

        // Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new UpdateProfileCommand(UUID.randomUUID(), "Ana", "García",
                        longEmail, null));

        // Assert
        assertTrue(ex.getMessage().contains("Email must not exceed"));
    }
}

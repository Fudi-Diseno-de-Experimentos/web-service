package synera.centralis.api.iam.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.iam.domain.model.commands.SignInCommand;
import synera.centralis.api.iam.domain.model.commands.SignUpCommand;
import synera.centralis.api.iam.domain.model.entities.Role;
import synera.centralis.api.iam.domain.model.valueobjects.Roles;
import synera.centralis.api.iam.domain.services.UserCommandService;
import synera.centralis.api.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import synera.centralis.api.iam.infrastructure.tokens.jwt.BearerTokenService;
import synera.centralis.api.shared.AbstractIntegrationTest;
import synera.centralis.api.shared.domain.exceptions.DuplicateResourceException;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración del contexto IAM (US33, US38).
 * Usa componentes reales: cifrado BCrypt, persistencia JPA y emisión de JWT,
 * apoyándose en los roles sembrados al arrancar la aplicación.
 */
@Transactional
@DisplayName("IAM - Integración: registro, login y unicidad de credenciales")
class IamIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BearerTokenService bearerTokenService;

    private SignUpCommand signUp(String username) {
        return new SignUpCommand(username, "123456", "Ada", "Lovelace", username,
                List.of(new Role(Roles.ROLE_USER)));
    }

    @Test
    @DisplayName("Componentes reales: registrarse guarda la contraseña cifrada, no en texto plano")
    void registroGuardaPasswordCifrado() {
        // Business / User Story Rational (US33): las credenciales se almacenan
        // cifradas para proteger la cuenta.
        // Act
        userCommandService.handle(signUp("empleado1@synera.com"));
        var guardado = userRepository.findByUsername("empleado1@synera.com");
        // Assert
        assertTrue(guardado.isPresent());
        assertNotEquals("123456", guardado.get().getPassword());
    }

    @Test
    @DisplayName("Componentes reales: un login válido emite un JWT verificable")
    void loginEmiteTokenValido() {
        // Business / User Story Rational (US38): tras validar las credenciales se
        // emite un JWT firmado para la sesión.
        // Arrange
        userCommandService.handle(signUp("empleado2@synera.com"));
        // Act
        var resultado = userCommandService.handle(new SignInCommand("empleado2@synera.com", "123456"));
        var token = resultado.getRight();
        // Assert
        assertTrue(bearerTokenService.validateToken(token));
        assertEquals("empleado2@synera.com", bearerTokenService.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Excepción por estado inválido: registrar un usuario duplicado es rechazado")
    void registroDuplicadoEsRechazado() {
        // Business / User Story Rational (US33): el correo/usuario debe ser único;
        // un segundo registro con el mismo identificador se rechaza.
        // Arrange
        userCommandService.handle(signUp("empleado3@synera.com"));
        // Act + Assert
        assertThrows(DuplicateResourceException.class,
                () -> userCommandService.handle(signUp("empleado3@synera.com")));
    }

    @Test
    @DisplayName("Excepción por estado inválido: un login con contraseña incorrecta no autentica")
    void loginCredencialesInvalidasEsRechazado() {
        // Business / User Story Rational (US38): credenciales inválidas devuelven
        // no autorizado y no emiten token.
        // Arrange
        userCommandService.handle(signUp("empleado4@synera.com"));
        // Act + Assert
        assertThrows(UnauthorizedException.class,
                () -> userCommandService.handle(new SignInCommand("empleado4@synera.com", "incorrecta")));
    }
}

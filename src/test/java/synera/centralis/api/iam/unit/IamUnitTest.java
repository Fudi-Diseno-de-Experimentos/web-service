package synera.centralis.api.iam.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.domain.model.entities.Role;
import synera.centralis.api.iam.domain.model.valueobjects.Roles;
import synera.centralis.api.iam.infrastructure.hashing.bcrypt.services.HashingServiceImpl;
import synera.centralis.api.iam.infrastructure.tokens.jwt.services.TokenServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios del contexto IAM (US33, US38, US39).
 * Se prueba el cifrado de contraseñas (BCrypt), la emisión/validación de JWT y
 * la asignación de roles. El flujo HTTP de autenticación se cubre en cucumber.
 */
@DisplayName("IAM - Seguridad: cifrado, JWT y roles (US33, US38, US39)")
class IamUnitTest {

    private static final String SECRET = "estaEsUnaClaveSecretaSimuladaParaTestingDeAlMenos32Caracteres";

    private HashingServiceImpl hashingService;

    @BeforeEach
    void setUp() throws Exception {
        // HashingServiceImpl tiene constructor package-private; se instancia por reflexión.
        var ctor = HashingServiceImpl.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        hashingService = ctor.newInstance();
    }

    private TokenServiceImpl tokenService(int expirationDays) {
        var service = new TokenServiceImpl();
        ReflectionTestUtils.setField(service, "secret", SECRET);
        ReflectionTestUtils.setField(service, "expirationDays", expirationDays);
        return service;
    }

    @Test
    @DisplayName("Happy: un JWT recién emitido es válido y conserva el username")
    void jwtEmitidoEsValido() {
        // Business / User Story Rational (US38): tras un login válido se emite un
        // JWT firmado que identifica al empleado.
        // Arrange
        var service = tokenService(7);
        // Act
        var token = service.generateToken("empleado@synera.com");
        // Assert
        assertTrue(service.validateToken(token));
        assertEquals("empleado@synera.com", service.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Excepción por estado inválido: un token manipulado no es válido")
    void tokenManipuladoEsInvalido() {
        // Business / User Story Rational (US38): un token sin firma válida debe
        // rechazarse para impedir accesos no autorizados.
        // Arrange
        var service = tokenService(7);
        // Act + Assert
        assertFalse(service.validateToken("esto.no.es.un.jwt"));
    }

    @Test
    @DisplayName("Límite: un token con expiración ya vencida es rechazado")
    void tokenExpiradoEsRechazado() {
        // Business / User Story Rational (US38): un JWT usado más allá de su
        // validez se rechaza por caducado.
        // Arrange: expiración negativa => el token nace vencido
        var service = tokenService(-1);
        // Act
        var token = service.generateToken("empleado@synera.com");
        // Assert
        assertFalse(service.validateToken(token));
    }

    @Test
    @DisplayName("Condicional A: una contraseña correcta coincide con su hash")
    void contrasenaCorrectaCoincide() {
        // Business / User Story Rational (US33/US38): las credenciales se guardan
        // cifradas y el login solo procede si coinciden.
        // Arrange
        var hash = hashingService.encode("Secreto123");
        // Act + Assert
        assertNotEquals("Secreto123", hash);
        assertTrue(hashingService.matches("Secreto123", hash));
    }

    @Test
    @DisplayName("Condicional B: una contraseña incorrecta no coincide con el hash")
    void contrasenaIncorrectaNoCoincide() {
        // Business / User Story Rational (US38): credenciales inválidas no deben
        // autenticar.
        // Arrange
        var hash = hashingService.encode("Secreto123");
        // Act + Assert
        assertFalse(hashingService.matches("Incorrecta", hash));
    }

    @Test
    @DisplayName("Integridad de datos: el mismo password produce hashes distintos (salt)")
    void hashesUsanSalt() {
        // Business / User Story Rational (US33): el cifrado con salt evita que dos
        // contraseñas iguales generen el mismo hash.
        // Act
        var hash1 = hashingService.encode("Secreto123");
        var hash2 = hashingService.encode("Secreto123");
        // Assert
        assertNotEquals(hash1, hash2);
        assertTrue(hashingService.matches("Secreto123", hash1));
        assertTrue(hashingService.matches("Secreto123", hash2));
    }

    @Test
    @DisplayName("Datos insuficientes: un usuario sin roles recibe ROLE_USER por defecto")
    void usuarioSinRolesRecibeRolPorDefecto() {
        // Business / User Story Rational (US39): la navegación por roles exige que
        // todo usuario tenga al menos el rol básico.
        // Act
        var user = new User("empleado@synera.com", "hash", List.of());
        // Assert
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName() == Roles.ROLE_USER));
    }

    @Test
    @DisplayName("Integridad de datos: validateRoleSet devuelve el rol por defecto si la lista es vacía")
    void validateRoleSetDevuelveDefault() {
        // Business / User Story Rational (US39): la regla de roles centraliza el
        // valor por defecto para evitar usuarios sin permisos.
        // Act
        var roles = Role.validateRoleSet(List.of());
        // Assert
        assertEquals(1, roles.size());
        assertEquals(Roles.ROLE_USER, roles.get(0).getName());
    }

    @Test
    @DisplayName("Condicional: agregar un rol de gerente lo añade al conjunto del usuario")
    void agregarRolGerente() {
        // Business / User Story Rational (US39): un empleado puede tener roles que
        // habilitan acciones de gestión.
        // Arrange
        var user = new User("gerente@synera.com", "hash");
        // Act
        user.addRole(new Role(Roles.ROLE_MANAGER));
        // Assert
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName() == Roles.ROLE_MANAGER));
    }
}

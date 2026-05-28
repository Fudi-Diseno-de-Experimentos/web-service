package synera.centralis.api.announcement.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import synera.centralis.api.announcement.domain.model.aggregates.Announcement;
import synera.centralis.api.announcement.domain.model.valueobjects.Priority;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios del contexto Announcement (US10-US13).
 * Se prueban las reglas de negocio e invariantes del agregado Announcement y
 * del value object Priority. No se prueban validaciones de parámetros de DTO.
 */
@DisplayName("Announcement - Reglas de negocio de anuncios (US10-US13)")
class AnnouncementUnitTest {

    private static final UUID AUTOR = UUID.randomUUID();

    @Test
    @DisplayName("Happy: un gerente publica un anuncio válido y queda registrado")
    void publicarAnuncioValido() {
        // Business / User Story Rational (US10): el gerente debe poder publicar
        // un anuncio con información relevante para informar a los empleados.
        // Arrange
        var priority = Priority.normal();
        // Act
        var anuncio = new Announcement("Cierre por feriado", "La oficina cerrará el lunes", null, priority, AUTOR);
        // Assert
        assertEquals("Cierre por feriado", anuncio.getTitle());
        assertEquals("La oficina cerrará el lunes", anuncio.getDescription());
        assertEquals(Priority.PriorityLevel.NORMAL, anuncio.getPriority().level());
        assertEquals(AUTOR, anuncio.getCreatedBy());
    }

    @Test
    @DisplayName("Límite superior: título de exactamente 200 caracteres es aceptado")
    void tituloEnLimiteMaximoEsValido() {
        // Business / User Story Rational (US10): el título admite hasta 200
        // caracteres; el borde exacto debe seguir siendo publicable.
        // Arrange
        var titulo = "T".repeat(200);
        // Act
        var anuncio = new Announcement(titulo, "contenido", null, Priority.normal(), AUTOR);
        // Assert
        assertEquals(200, anuncio.getTitle().length());
    }

    @Test
    @DisplayName("Límite superior: título de 201 caracteres es rechazado")
    void tituloSobreLimiteEsRechazado() {
        // Business / User Story Rational (US10): superar el máximo de 200 rompe
        // la invariante de tamaño del anuncio.
        // Arrange
        var titulo = "T".repeat(201);
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Announcement(titulo, "contenido", null, Priority.normal(), AUTOR));
        assertEquals("Title cannot exceed 200 characters", ex.getMessage());
    }

    @Test
    @DisplayName("Límite superior: descripción de 5001 caracteres es rechazada")
    void descripcionSobreLimiteEsRechazada() {
        // Business / User Story Rational (US10): la descripción no puede exceder
        // 5000 caracteres para mantener anuncios manejables.
        // Arrange
        var descripcion = "D".repeat(5001);
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Announcement("Titulo", descripcion, null, Priority.normal(), AUTOR));
        assertEquals("Description cannot exceed 5000 characters", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción por datos insuficientes: descripción nula impide crear el anuncio")
    void descripcionNulaImpideCrear() {
        // Business / User Story Rational (US10): un anuncio sin contenido no
        // informa nada, por lo que es un estado inválido de negocio.
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Announcement("Titulo", null, null, Priority.normal(), AUTOR));
        assertEquals("Description cannot be null or empty", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción por estado inválido: no se puede degradar la prioridad a nula")
    void actualizarPrioridadNulaEsRechazado() {
        // Business / User Story Rational (US11): un anuncio siempre debe tener
        // una prioridad definida para ordenarse correctamente.
        // Arrange
        var anuncio = new Announcement("Titulo", "contenido", null, Priority.high(), AUTOR);
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> anuncio.updatePriority(null));
    }

    @Test
    @DisplayName("Condicional A: un anuncio HIGH o URGENT se considera destacado")
    void anuncioPrioritarioEsDestacado() {
        // Business / User Story Rational (US11): los anuncios prioritarios se
        // muestran en la sección destacada.
        // Arrange
        var alto = new Announcement("A", "c", null, Priority.high(), AUTOR);
        var urgente = new Announcement("B", "c", null, Priority.urgent(), AUTOR);
        var normal = new Announcement("C", "c", null, Priority.normal(), AUTOR);
        // Act + Assert
        assertTrue(alto.isHighPriorityOrUrgent());
        assertTrue(urgente.isHighPriorityOrUrgent());
        assertFalse(normal.isHighPriorityOrUrgent());
    }

    @Test
    @DisplayName("Condicional B: solo URGENT dispara la urgencia (notificación urgente)")
    void soloUrgenteEsUrgente() {
        // Business / User Story Rational (US11): la notificación urgente a todos
        // los empleados se reserva para la prioridad URGENT.
        // Arrange
        var urgente = new Announcement("A", "c", null, Priority.urgent(), AUTOR);
        var alto = new Announcement("B", "c", null, Priority.high(), AUTOR);
        // Act + Assert
        assertTrue(urgente.isUrgent());
        assertFalse(alto.isUrgent());
    }

    @Test
    @DisplayName("Integridad de datos: editar un anuncio actualiza y recorta sus campos")
    void editarAnuncioActualizaCampos() {
        // Business / User Story Rational (US12): el gerente edita un anuncio y los
        // cambios se reflejan inmediatamente, sin espacios sobrantes.
        // Arrange
        var anuncio = new Announcement("Viejo", "viejo contenido", null, Priority.normal(), AUTOR);
        // Act
        anuncio.update("  Nuevo Titulo  ", "Nuevo contenido", "http://img/x.png", Priority.urgent());
        // Assert
        assertEquals("Nuevo Titulo", anuncio.getTitle());
        assertEquals("Nuevo contenido", anuncio.getDescription());
        assertEquals("http://img/x.png", anuncio.getImage());
        assertTrue(anuncio.isUrgent());
    }

    @Test
    @DisplayName("Integridad de datos: Priority no admite nivel nulo")
    void priorityNoAdmiteNivelNulo() {
        // Business / User Story Rational (US11): el value object de prioridad
        // protege su propia invariante de nivel obligatorio.
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new Priority(null));
    }
}

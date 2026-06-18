package synera.centralis.api.event.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.domain.model.valueobjects.RecipientStatus;
import synera.centralis.api.event.domain.model.valueobjects.SpaceId;
import synera.centralis.api.event.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios del contexto Event (US18-US21).
 * Se prueban las invariantes y el comportamiento del agregado Event.
 */
@DisplayName("Event - Reglas de negocio de eventos (US18-US21)")
class EventUnitTest {

    private static final UserId CREADOR = new UserId(UUID.randomUUID());
    private static final SpaceId SALA = new SpaceId(UUID.randomUUID());
    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 10, 10, 0);

    private Event eventoValido() {
        return new Event("Reunión General", "Alineación trimestral", FECHA, SALA,
                List.of(UUID.randomUUID()), CREADOR);
    }

    @Test
    @DisplayName("Happy: el gerente crea un evento con un invitado y queda registrado")
    void crearEventoValido() {
        // Business / User Story Rational (US18): el gerente crea un evento con los
        // datos necesarios y se muestra a los empleados seleccionados.
        // Act
        var evento = eventoValido();
        // Assert
        assertEquals("Reunión General", evento.getTitle());
        assertEquals(FECHA, evento.getDate());
        assertEquals(1, evento.getRecipientCount());
    }

    @Test
    @DisplayName("Límite superior: título de exactamente 200 caracteres es aceptado")
    void tituloEnLimiteMaximoEsValido() {
        // Business / User Story Rational (US18): el título admite hasta 200
        // caracteres; el borde exacto debe poder guardarse.
        // Arrange + Act
        var evento = new Event("T".repeat(200), "desc", FECHA, SALA, List.of(UUID.randomUUID()), CREADOR);
        // Assert
        assertEquals(200, evento.getTitle().length());
    }

    @Test
    @DisplayName("Límite superior: descripción de 1001 caracteres es rechazada")
    void descripcionSobreLimiteEsRechazada() {
        // Business / User Story Rational (US18): la descripción del evento no
        // puede exceder 1000 caracteres.
        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Event("Titulo", "D".repeat(1001), FECHA, SALA, List.of(UUID.randomUUID()), CREADOR));
    }

    @Test
    @DisplayName("Límite inferior / datos insuficientes: un evento sin invitados es inválido")
    void eventoSinInvitadosEsInvalido() {
        // Business / User Story Rational (US18): un evento debe mostrarse a
        // empleados seleccionados; sin invitados no tiene sentido.
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Event("Titulo", "desc", FECHA, SALA, List.of(), CREADOR));
        assertEquals("Event must have at least one recipient", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción por estado inválido: no se puede agregar un invitado nulo")
    void agregarInvitadoNuloEsRechazado() {
        // Business / User Story Rational (US18): la lista de invitados debe
        // mantenerse íntegra; un id nulo es un estado inválido.
        // Arrange
        var evento = eventoValido();
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> evento.addRecipient(null));
    }

    @Test
    @DisplayName("Condicional A: editar solo la fecha conserva el resto de datos")
    void editarSoloFechaConservaResto() {
        // Business / User Story Rational (US20): el gerente pospone un evento
        // cambiando la fecha; el resto de la información permanece.
        // Arrange
        var evento = eventoValido();
        var nuevaFecha = FECHA.plusDays(7);
        // Act
        evento.updateEvent(null, null, nuevaFecha, null, null);
        // Assert
        assertEquals(nuevaFecha, evento.getDate());
        assertEquals("Reunión General", evento.getTitle());
        assertEquals(1, evento.getRecipientCount());
    }

    @Test
    @DisplayName("Condicional B: isRecipient distingue invitados de no invitados")
    void isRecipientDistingueInvitados() {
        // Business / User Story Rational (US18): solo los empleados seleccionados
        // ven el evento.
        // Arrange
        var invitado = UUID.randomUUID();
        var evento = new Event("Titulo", "desc", FECHA, SALA, List.of(invitado), CREADOR);
        // Act + Assert
        assertTrue(evento.isRecipient(new UserId(invitado)));
        assertFalse(evento.isRecipient(new UserId(UUID.randomUUID())));
    }

    @Test
    @DisplayName("Integridad de datos: agregar y quitar invitados ajusta el conteo")
    void agregarYQuitarInvitadosAjustaConteo() {
        // Business / User Story Rational (US20): al modificar invitados, el
        // conteo de asistentes esperados se mantiene consistente.
        // Arrange
        var evento = eventoValido();
        var nuevo = new UserId(UUID.randomUUID());
        // Act
        evento.addRecipient(nuevo);
        // Assert
        assertEquals(2, evento.getRecipientCount());
        // Act
        evento.removeRecipient(nuevo);
        // Assert
        assertEquals(1, evento.getRecipientCount());
    }

    @Test
    @DisplayName("Invitación: un invitado nuevo arranca en PENDING")
    void invitadoNuevoArrancaEnPending() {
        // Business Rational: al crear el evento nadie ha respondido todavía.
        var invitado = UUID.randomUUID();
        var evento = new Event("Titulo", "desc", FECHA, SALA, List.of(invitado), CREADOR);
        assertEquals(RecipientStatus.PENDING, evento.getStatusFor(invitado));
    }

    @Test
    @DisplayName("Invitación: aceptar y luego cancelar actualiza el estado del invitado")
    void aceptarYLuegoCancelarActualizaEstado() {
        // Business Rational: el miembro acepta y más tarde puede cancelar (declinar).
        var invitado = UUID.randomUUID();
        var evento = new Event("Titulo", "desc", FECHA, SALA, List.of(invitado), CREADOR);
        // Act
        evento.respondToInvitation(invitado, RecipientStatus.ACCEPTED);
        // Assert
        assertEquals(RecipientStatus.ACCEPTED, evento.getStatusFor(invitado));
        // Act
        evento.respondToInvitation(invitado, RecipientStatus.DECLINED);
        // Assert
        assertEquals(RecipientStatus.DECLINED, evento.getStatusFor(invitado));
    }

    @Test
    @DisplayName("Invitación: responder como no-invitado es un estado inválido")
    void responderComoNoInvitadoEsRechazado() {
        // Business Rational: solo un invitado puede responder, y solo por sí mismo.
        var evento = eventoValido();
        assertThrows(IllegalStateException.class,
                () -> evento.respondToInvitation(UUID.randomUUID(), RecipientStatus.ACCEPTED));
    }

    @Test
    @DisplayName("Edición: actualizar invitados conserva la respuesta de los que permanecen")
    void actualizarInvitadosConservaRespuestas() {
        // Business Rational (US20): editar el evento no debe borrar las respuestas
        // de quienes siguen invitados.
        var permanece = UUID.randomUUID();
        var seVa = UUID.randomUUID();
        var nuevo = UUID.randomUUID();
        var evento = new Event("Titulo", "desc", FECHA, SALA, List.of(permanece, seVa), CREADOR);
        evento.respondToInvitation(permanece, RecipientStatus.ACCEPTED);
        evento.respondToInvitation(seVa, RecipientStatus.DECLINED);
        // Act: la nueva lista mantiene a 'permanece', quita a 'seVa', agrega a 'nuevo'
        evento.updateEvent(null, null, null, null, List.of(permanece, nuevo));
        // Assert
        assertEquals(RecipientStatus.ACCEPTED, evento.getStatusFor(permanece), "respuesta conservada");
        assertEquals(RecipientStatus.PENDING, evento.getStatusFor(nuevo), "nuevo arranca PENDING");
        assertNull(evento.getStatusFor(seVa), "removido ya no es invitado");
        assertEquals(2, evento.getRecipientCount());
    }

    @Test
    @DisplayName("Edición: un invitado removido y vuelto a agregar regresa en PENDING")
    void removidoYReagregadoRegresaEnPending() {
        // Business Rational: re-agregar a alguien no recupera su respuesta previa.
        var invitado = UUID.randomUUID();
        var otro = UUID.randomUUID();
        var evento = new Event("Titulo", "desc", FECHA, SALA, List.of(invitado, otro), CREADOR);
        evento.respondToInvitation(invitado, RecipientStatus.ACCEPTED);
        // Act: quitar a 'invitado'...
        evento.updateEvent(null, null, null, null, List.of(otro));
        assertNull(evento.getStatusFor(invitado));
        // ...y volver a agregarlo
        evento.updateEvent(null, null, null, null, List.of(otro, invitado));
        // Assert
        assertEquals(RecipientStatus.PENDING, evento.getStatusFor(invitado));
    }
}

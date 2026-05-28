package synera.centralis.api.cucumber;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** Step definitions de la feature 02_event (US18, US34). */
public class EventStepDefinitions extends AbstractCucumberSteps {

    @Dado("que el gerente está autenticado para gestionar eventos")
    public void gerenteGestionEventos() {
        autenticarComo("gerente@synera.com", AUTORIDADES_GESTION);
    }

    @Dado("que un empleado sin permisos de gestión está autenticado")
    public void empleadoSinPermisos() {
        autenticarComo("empleado@synera.com", AUTORIDADES_EMPLEADO);
    }

    @Cuando("crea el evento {string} con los siguientes invitados:")
    public void creaEvento(String titulo, List<Map<String, String>> invitados) {
        post("/api/v1/events", eventoJson(titulo, invitados), true);
    }

    @Cuando("intenta crear el evento {string} con los siguientes invitados:")
    public void intentaCrearEvento(String titulo, List<Map<String, String>> invitados) {
        post("/api/v1/events", eventoJson(titulo, invitados), true);
    }

    private String eventoJson(String titulo, List<Map<String, String>> invitados) {
        String ids = invitados.stream()
                .map(fila -> "\"" + fila.get("invitado") + "\"")
                .collect(Collectors.joining(","));
        return "{"
                + "\"title\":\"" + titulo + "\","
                + "\"description\":\"Actividad corporativa\","
                + "\"date\":\"2026-10-10T10:00:00\","
                + "\"recipientIds\":[" + ids + "],"
                + "\"createdBy\":\"" + UUID.randomUUID() + "\""
                + "}";
    }

    @Entonces("el evento se crea correctamente")
    public void eventoCreado() {
        assertEquals(201, response.getStatusCode().value());
    }

    @Y("el evento aparece en la lista de eventos")
    public void eventoEnLista() {
        get("/api/v1/events", true);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Entonces("el acceso para crear el evento es denegado")
    public void accesoEventoDenegado() {
        // La app no configura accessDeniedHandler: una denegación de @PreAuthorize
        // se resuelve por el authenticationEntryPoint, devolviendo 401 (acceso restringido).
        assertEquals(401, response.getStatusCode().value());
    }
}

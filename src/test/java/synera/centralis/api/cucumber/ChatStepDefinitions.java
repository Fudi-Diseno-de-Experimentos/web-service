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

/** Step definitions de la feature 03_chat (US23). */
public class ChatStepDefinitions extends AbstractCucumberSteps {

    @Dado("que el empleado está autenticado en el chat")
    public void empleadoAutenticadoChat() {
        autenticarComo("empleado@synera.com", AUTORIDADES_GESTION);
    }

    @Cuando("crea el grupo {string} con visibilidad {string} y los miembros:")
    public void creaGrupo(String nombre, String visibilidad, List<Map<String, String>> miembros) {
        String ids = miembros.stream()
                .map(fila -> "\"" + fila.get("miembro") + "\"")
                .collect(Collectors.joining(","));
        String body = "{"
                + "\"name\":\"" + nombre + "\","
                + "\"visibility\":\"" + visibilidad + "\","
                + "\"memberIds\":[" + ids + "],"
                + "\"createdBy\":\"" + UUID.randomUUID() + "\""
                + "}";
        post("/api/v1/groups", body, true);
    }

    @Entonces("el grupo se crea correctamente")
    public void grupoCreado() {
        assertEquals(201, response.getStatusCode().value());
    }

    @Y("el grupo queda disponible para sus miembros")
    public void grupoDisponible() {
        assertNotNull(campo("id"));
    }
}

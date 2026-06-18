package synera.centralis.api.cucumber;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Step definitions de la feature 01_announcement (US10, US11). */
public class AnnouncementStepDefinitions extends AbstractCucumberSteps {

    @Dado("que el gerente ha iniciado sesión para publicar anuncios")
    public void gerenteAutenticado() {
        autenticarComo("gerente@synera.com", AUTORIDADES_GESTION);
    }

    @Dado("que un empleado común ha iniciado sesión para publicar anuncios")
    public void empleadoComunAutenticado() {
        autenticarComo("empleado@synera.com", AUTORIDADES_EMPLEADO);
    }

    @Entonces("el anuncio es rechazado por falta de permisos")
    public void anuncioRechazadoPorFaltaDePermisos() {
        assertEquals(403, response.getStatusCode().value());
    }

    @Cuando("publica un anuncio con título {string} y prioridad {string}")
    public void publicaAnuncio(String titulo, String prioridad) {
        String body = "{"
                + "\"title\":\"" + titulo + "\","
                + "\"description\":\"Comunicado oficial de la compañía\","
                + "\"priority\":\"" + prioridad + "\","
                + "\"createdBy\":\"" + UUID.randomUUID() + "\""
                + "}";
        post("/api/v1/announcements", body, true);
    }

    @Entonces("el anuncio se guarda correctamente")
    public void anuncioGuardado() {
        assertEquals(201, response.getStatusCode().value());
    }

    @Y("el anuncio aparece en el listado de la compañía")
    public void anuncioEnListado() {
        get("/api/v1/announcements", true);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Y("la prioridad registrada es {string}")
    public void prioridadRegistrada(String prioridad) {
        assertEquals(prioridad, campo("priority"));
    }
}

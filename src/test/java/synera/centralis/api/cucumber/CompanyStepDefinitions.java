package synera.centralis.api.cucumber;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Step definitions de la feature 04_company (US41). */
public class CompanyStepDefinitions extends AbstractCucumberSteps {

    @Dado("que el representante legal está autenticado")
    public void representanteAutenticado() {
        autenticarComo("representante@synera.com", AUTORIDADES_GESTION);
    }

    @Cuando("registra la compañía {string} con RUC {string}")
    public void registraCompania(String nombre, String ruc) {
        String body = "{"
                + "\"ruc\":\"" + ruc + "\","
                + "\"nombre\":\"" + nombre + "\","
                + "\"isActive\":true,"
                + "\"userId\":\"" + UUID.randomUUID() + "\""
                + "}";
        post("/api/v1/companies", body, true);
    }

    @Entonces("la compañía se registra correctamente")
    public void companiaRegistrada() {
        assertEquals(201, response.getStatusCode().value());
    }

    @Y("el nombre registrado es {string}")
    public void nombreRegistrado(String nombre) {
        assertEquals(nombre, campo("nombre"));
    }
}

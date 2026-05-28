package synera.centralis.api.cucumber;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static org.junit.jupiter.api.Assertions.*;

/** Step definitions de la feature 05_iam (US34). */
public class IamStepDefinitions extends AbstractCucumberSteps {

    @Dado("que un empleado autenticado solicita el recurso de compañías")
    public void empleadoAutenticado() {
        autenticarComo("empleado@synera.com", AUTORIDADES_EMPLEADO);
    }

    @Dado("que un visitante sin token solicita el recurso de compañías")
    public void visitanteSinToken() {
        sinAutenticacion();
    }

    @Cuando("consulta el listado de compañías")
    public void consultaCompanias() {
        get("/api/v1/companies", true);
    }

    @Entonces("obtiene una respuesta exitosa")
    public void respuestaExitosa() {
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Entonces("la solicitud es rechazada por falta de autenticación")
    public void rechazadaSinAutenticacion() {
        assertEquals(401, response.getStatusCode().value());
    }
}

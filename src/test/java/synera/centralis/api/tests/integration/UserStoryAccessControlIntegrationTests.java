package synera.centralis.api.tests.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de control de acceso y lectura, levantando el
 * contexto completo de Spring sobre H2 (perfil "test").
 * Cubre US34 (restricción de acceso a la API), US41 (lectura del registro de
 * compañías) y US44 (lectura de perfiles vinculados a la compañía).
 * Patrón AAA.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserStoryAccessControlIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IamContextFacade iamContextFacade;

    @Test
    @DisplayName("US34: Una petición sin autenticar a un recurso protegido es rechazada")
    void shouldRejectUnauthenticatedRequest() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("US41: Un gerente autenticado lista las compañías registradas")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void shouldListCompanies_WhenAuthenticated() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US44: Un usuario autenticado consulta los perfiles disponibles")
    @WithMockUser(username = "employeeA", roles = "EMPLOYEE")
    void shouldListProfiles_WhenAuthenticated() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/profiles"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("US41: Consultar una compañía inexistente devuelve 404")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void shouldReturnNotFound_WhenCompanyDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/v1/companies/" + nonExistentId))
                .andExpect(status().isNotFound());
    }
}

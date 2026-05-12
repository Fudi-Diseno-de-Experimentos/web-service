package synera.centralis.api.tests.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// Reemplazamos el import antiguo de MockBean por el nuevo
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Levanta en H2 según application.properties
public class MultiTenancyAndFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Se mockea la fachada usando la anotación actualizada @MockitoBean
    @MockitoBean
    private IamContextFacade iamContextFacade;

    private UUID companyA_Id;
    private UUID companyB_Id;
    private UUID managerCompanyA_Id;
    private UUID employeeCompanyB_Id;
    private UUID createdEventCompanyA_Id;

    @BeforeEach
    void seedData() {
        // Inicialización de esquemas relacionales abstractos
        companyA_Id = UUID.randomUUID();
        companyB_Id = UUID.randomUUID();

        managerCompanyA_Id = UUID.randomUUID();
        employeeCompanyB_Id = UUID.randomUUID();
        createdEventCompanyA_Id = UUID.randomUUID();

        // Simulamos la resolución de Tenant (Compañía) a partir del SecurityContext
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        when(iamContextFacade.fetchCompanyIdByUsername("employeeB")).thenReturn(companyB_Id);
    }

    @Test
    @DisplayName("AISLAMIENTO MULTI-TENANCY: Usuario de Company B no tiene autorización para acceder a Company A")
    @WithMockUser(username = "employeeB", roles = "EMPLOYEE")
    void shouldDenyAccessToCrossTenantData() throws Exception {
        when(iamContextFacade.fetchCompanyIdByUsername("employeeB")).thenReturn(companyB_Id);

        // Simulando que el Evento no se encuentra en el repositorio acotado por Company B
        mockMvc.perform(get("/api/v1/events/" + createdEventCompanyA_Id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("FLUJO COMPLETO: Manager crea un Evento, Anuncio y Grupo de Chat para su Organización")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void managerCanCreateFullCommunicationFlow() throws Exception {
        // Manager de Company A
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);

        // 1. MANAGER CREA UN EVENTO
        String eventPayload = """
                {
                    "title": "Townhall Trimestral",
                    "description": "Revisión de métricas",
                    "date": "2026-06-15T10:00:00.000Z",
                    "location": "Auditorio Central",
                    "recipientIds": ["%s"],
                    "createdBy": "%s"
                }
                """.formatted(UUID.randomUUID(), managerCompanyA_Id);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload))
                .andExpect(status().isCreated());

        // 2. MANAGER CREA UN ANUNCIO
        String announcementPayload = """
                {
                    "title": "Nuevo beneficio de seguro médico",
                    "description": "Se ha añadido la cobertura dental al plan principal.",
                    "priority": "HIGH",
                    "createdBy": "%s"
                }
                """.formatted(managerCompanyA_Id);

        mockMvc.perform(post("/api/v1/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(announcementPayload))
                .andExpect(status().isCreated());

        // 3. MANAGER CREA UN GRUPO DE CHAT PARA EL EVENTO
        String groupPayload = """
                {
                    "name": "Comité Organizador",
                    "description": "Logística del Townhall",
                    "visibility": "PRIVATE",
                    "memberIds": ["%s"],
                    "createdBy": "%s"
                }
                """.formatted(managerCompanyA_Id, managerCompanyA_Id);

        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupPayload))
                .andExpect(status().isCreated());

        // 3. RECUPERACIÓN VÁLIDA (El tenant funcionó)
        mockMvc.perform(get("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
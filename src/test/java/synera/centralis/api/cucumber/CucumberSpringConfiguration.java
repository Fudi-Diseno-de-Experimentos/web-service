package synera.centralis.api.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import synera.centralis.api.WebServicesApplication;
import synera.centralis.api.shared.TestDatasourceProperties;
import synera.centralis.api.iam.infrastructure.tokens.jwt.BearerTokenService;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.notification.application.outboundservices.FirebaseCloudMessagingService;
import synera.centralis.api.notification.infrastructure.messaging.config.FirebaseConfiguration;

/**
 * Configuración compartida del contexto Spring para la suite Cucumber.
 *
 * Arranca la aplicación completa en un puerto aleatorio (las step definitions
 * usan {@code TestRestTemplate} para hablar HTTP real). La autenticación/IAM se
 * sustituye por mocks como parte del harness de negocio: las step definitions
 * controlan la identidad directamente y los escenarios se centran en la lógica
 * de los endpoints, no en el flujo criptográfico de JWT (eso se cubre en los
 * tests unitarios e integración del contexto iam).
 *
 * Firebase se mockea para que el contexto cargue sin credenciales y las
 * notificaciones queden fuera del alcance de los tests.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = WebServicesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @MockBean
    private BearerTokenService tokenService;

    @MockBean(name = "defaultUserDetailsService")
    private UserDetailsService userDetailsService;

    @MockBean
    private IamContextFacade iamContextFacade;

    @MockBean
    private FirebaseConfiguration firebaseConfiguration;

    @MockBean
    private FirebaseCloudMessagingService firebaseCloudMessagingService;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        TestDatasourceProperties.apply(registry);
    }
}

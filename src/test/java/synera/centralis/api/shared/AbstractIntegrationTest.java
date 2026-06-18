package synera.centralis.api.shared;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import synera.centralis.api.notification.application.outboundservices.FirebaseCloudMessagingService;
import synera.centralis.api.notification.infrastructure.messaging.config.FirebaseConfiguration;

/**
 * Base para todos los tests de integración (@SpringBootTest).
 *
 * El bean {@link FirebaseConfiguration} resuelve la credencial real de Firebase
 * en su @PostConstruct, lo que impediría arrancar el contexto en tests sin
 * secreto. Se sustituye por un mock (igual que el servicio externo de envío FCM)
 * para que el contexto cargue de forma hermética y las notificaciones queden
 * totalmente fuera del alcance de los tests. Este mock del servicio externo
 * cubre además el requisito de "una integración con @MockBean en servicio externo".
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    @MockBean
    protected FirebaseConfiguration firebaseConfiguration;

    @MockBean
    protected FirebaseCloudMessagingService firebaseCloudMessagingService;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        TestDatasourceProperties.apply(registry);
    }
}

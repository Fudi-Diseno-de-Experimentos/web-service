package synera.centralis.api.notification.infrastructure.messaging.config;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// @Configuration
public class FirebaseConfiguration {

//
//    @Value("${firebase.project.id}")
//    private String projectId;
//    
//    @Value("${firebase.database.url}")
//    private String databaseUrl;
//    
//    @Value("${firebase.private.key.id}")
//    private String privateKeyId;
//    
//    @Value("${firebase.private.key}")
//    private String privateKey;
//    
//    @Value("${firebase.client.email}")
//    private String clientEmail;
//    
//    @Value("${firebase.client.id}")
//    private String clientId;
//    
//    @Value("${firebase.auth.uri}")
//    private String authUri;
//    
//    @Value("${firebase.token.uri}")
//    private String tokenUri;
//    
//    @Value("${firebase.auth.provider.x509.cert.url}")
//    private String authProviderX509CertUrl;
//    
//    @Value("${firebase.client.x509.cert.url}")
//    private String clientX509CertUrl;
//    
//    @PostConstruct
//    public void initialize() {
//        try {
//            if (FirebaseApp.getApps().isEmpty()) {
//                // Create the service account JSON from environment variables
//                String serviceAccountJson = String.format(
//                    "{\n" +
//                    "  \"type\": \"service_account\",\n" +
//                    "  \"project_id\": \"%s\",\n" +
//                    "  \"private_key_id\": \"%s\",\n" +
//                    "  \"private_key\": \"%s\",\n" +
//                    "  \"client_email\": \"%s\",\n" +
//                    "  \"client_id\": \"%s\",\n" +
//                    "  \"auth_uri\": \"%s\",\n" +
//                    "  \"token_uri\": \"%s\",\n" +
//                    "  \"auth_provider_x509_cert_url\": \"%s\",\n" +
//                    "  \"client_x509_cert_url\": \"%s\",\n" +
//                    "  \"universe_domain\": \"googleapis.com\"\n" +
//                    "}",
//                    projectId, privateKeyId, privateKey, clientEmail, clientId,
//                    authUri, tokenUri, authProviderX509CertUrl, clientX509CertUrl
//                );
//                
//                GoogleCredentials credentials = GoogleCredentials
//                        .fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)));
//                
//                FirebaseOptions options = FirebaseOptions.builder()
//                        .setCredentials(credentials)
//                        .setProjectId(projectId) 
//                        .setDatabaseUrl(databaseUrl)
//                        .build();
//                
//                FirebaseApp.initializeApp(options);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to initialize Firebase", e);
//        }
//    }
//    
//    // @Bean
//    public FirebaseApp firebaseApp() {
//        return FirebaseApp.getInstance();
//    }
//
//}
}

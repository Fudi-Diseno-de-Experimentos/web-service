package synera.centralis.api.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import synera.centralis.api.iam.infrastructure.tokens.jwt.BearerTokenService;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Base compartida para las step definitions Cucumber.
 *
 * Habla HTTP real contra el servidor en puerto aleatorio mediante
 * {@link TestRestTemplate}. La autenticación se controla mockeando
 * {@link BearerTokenService}, el {@link UserDetailsService} y
 * {@link IamContextFacade}: las step definitions deciden la identidad y los
 * roles de cada escenario.
 */
public abstract class AbstractCucumberSteps {

    protected static final String TOKEN = "mock-token";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected BearerTokenService tokenService;

    @Autowired
    @Qualifier("defaultUserDetailsService")
    protected UserDetailsService userDetailsService;

    @Autowired
    protected IamContextFacade iamContextFacade;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ResponseEntity<String> response;
    protected UUID companyId;

    /** Autoridades amplias: cubren tanto hasRole('MANAGER') como hasAnyRole('ROLE_MANAGER'). */
    protected static final String[] AUTORIDADES_GESTION = {
            "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER",
            "ROLE_ROLE_MANAGER", "ROLE_ROLE_ADMIN"
    };

    /** Solo empleado: sin permisos de gestión. */
    protected static final String[] AUTORIDADES_EMPLEADO = { "ROLE_EMPLOYEE", "ROLE_USER" };

    protected void autenticarComo(String username, String... authorities) {
        when(tokenService.getBearerTokenFrom(any())).thenReturn(TOKEN);
        when(tokenService.validateToken(anyString())).thenReturn(true);
        when(tokenService.getUsernameFromToken(anyString())).thenReturn(username);

        var auths = Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
        UserDetails dummy = new User(username, "password", auths);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(dummy);

        companyId = UUID.randomUUID();
        when(iamContextFacade.fetchCompanyIdByUsername(anyString())).thenReturn(companyId);
    }

    /** Simula una solicitud sin token (visitante no autenticado). */
    protected void sinAutenticacion() {
        when(tokenService.getBearerTokenFrom(any())).thenReturn(null);
    }

    protected HttpHeaders headers(boolean conToken) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        if (conToken) h.setBearerAuth(TOKEN);
        return h;
    }

    protected void post(String url, String body, boolean conToken) {
        response = restTemplate.exchange(url, HttpMethod.POST,
                new HttpEntity<>(body, headers(conToken)), String.class);
    }

    protected void get(String url, boolean conToken) {
        response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers(conToken)), String.class);
    }

    protected String campo(String jsonField) {
        try {
            return objectMapper.readTree(response.getBody()).get(jsonField).asText();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer el campo '" + jsonField + "' de: " + response.getBody(), e);
        }
    }
}

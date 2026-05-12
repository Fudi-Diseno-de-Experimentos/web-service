package synera.centralis.api.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import synera.centralis.api.WebServicesApplication;
import synera.centralis.api.iam.infrastructure.tokens.jwt.BearerTokenService;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

@CucumberContextConfiguration
@SpringBootTest(classes = WebServicesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {
    @MockBean
    private BearerTokenService tokenService;

    @MockBean(name = "defaultUserDetailsService")
    private UserDetailsService userDetailsService;
    
    @MockBean
    private IamContextFacade iamContextFacade;
}

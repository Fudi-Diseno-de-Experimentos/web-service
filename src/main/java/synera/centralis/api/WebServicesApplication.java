package synera.centralis.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class WebServicesApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebServicesApplication.class);

	public static void main(String[] args) {

        SpringApplication.run(WebServicesApplication.class, args);
        LOGGER.info("Swagger UI available at: http://localhost:8080/swagger-ui/index.html");
	}

}

package synera.centralis.api.shared;

import org.springframework.test.context.DynamicPropertyRegistry;

/**
 * Forces the in-memory H2 datasource for the Spring TestContext.
 *
 * The {@code springboot3-dotenv} dependency loads the root {@code .env} into the
 * environment, and its {@code SPRING_DATASOURCE_*} values override the test
 * {@code application.properties} — which would point every {@code @SpringBootTest}
 * at the real Supabase Postgres and break context loading. Registering the
 * datasource through {@link DynamicPropertyRegistry} wins over the dotenv property
 * source, so the suite runs hermetically against H2 regardless of whether a
 * {@code .env} is present on the machine.
 */
public final class TestDatasourceProperties {

    private TestDatasourceProperties() {
    }

    public static void apply(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;NON_KEYWORDS=USER");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }
}

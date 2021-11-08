package de.slevermann.cocktails.backend;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Tests that need to run against a postgres database should inherit from this class.
 * <p>
 * It starts a docker container and sets the required spring properties to use it in testing.
 * <p>
 * If the environment variable NO_DOCKER is set, the tests are run without starting a docker container,
 * and it is expected that the user supplies their own database and configuration for Spring.
 */
@ContextConfiguration(initializers = ContainerTestBase.Initializer.class)
public abstract class ContainerTestBase {

    public static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:12.2-alpine");

    static {
        if (System.getenv("NO_DOCKER") == null) {
            postgres.start();
        }
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            if (System.getenv("NO_DOCKER") == null) {
                TestPropertyValues.of(
                        "spring.datasource.url=" + postgres.getJdbcUrl(),
                        "spring.datasource.username=" + postgres.getUsername(),
                        "spring.datasource.password=" + postgres.getPassword()
                ).applyTo(configurableApplicationContext.getEnvironment());
            }
        }
    }

}

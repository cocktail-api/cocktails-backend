package de.slevermann.cocktails.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@Configuration
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
public class ProblemConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer(@Value("${server.error.include-stacktrace}")
                                                                        ErrorProperties.IncludeAttribute includeStacktrace) {
        return builder -> builder.modules(new ProblemModule()
                        .withStackTraces(includeStacktrace == ErrorProperties.IncludeAttribute.ALWAYS),
                new ConstraintViolationProblemModule());
    }

}

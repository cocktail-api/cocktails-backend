package de.slevermann.cocktails.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class ExceptionHandler implements ProblemHandling {

    private final ErrorProperties.IncludeAttribute includeStacktraces;

    public ExceptionHandler(@Value("${server.error.include-stacktrace}")
                                    ErrorProperties.IncludeAttribute includeStacktraces) {
        this.includeStacktraces = includeStacktraces;
    }


    @Override
    public boolean isCausalChainsEnabled() {
        return includeStacktraces == ErrorProperties.IncludeAttribute.ALWAYS;
    }
}

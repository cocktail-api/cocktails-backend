package de.slevermann.cocktails.backend.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.statement.Binding;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

import static net.logstash.logback.argument.StructuredArguments.*;

@Component
@Slf4j
public class JdbiLogger implements SqlLogger {

    private static final String ERROR_TEMPLATE_PARAMETERS = "Exception running query '{}' with parameters '{}'";
    private static final String ERROR_TEMPLATE = "Exception running query '{}'";

    private static final String TEMPLATE_PARAMETERS = "Executed query '{}' with parameters '{}'";
    private static final String TEMPLATE = "Executed query '{}'";


    private final JdbiConfigurationProperties configurationProperties;

    public JdbiLogger(final JdbiConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Override
    public void logException(final StatementContext context, final SQLException ex) {
        if (log.isErrorEnabled()) {
            final var sql = chompWhitespace(context.getRenderedSql());
            if (configurationProperties.isShowParameters()) {
                log.error(ERROR_TEMPLATE_PARAMETERS, kv("query", sql), kv("parameters", getParameters(context)), ex);
            } else {
                log.error(ERROR_TEMPLATE, kv("query", sql), ex);
            }
        }
    }

    @Override
    public void logAfterExecution(final StatementContext context) {
        if (log.isInfoEnabled()) {
            final var sql = chompWhitespace(context.getRenderedSql());
            if (configurationProperties.isShowParameters()) {
                log.info(TEMPLATE_PARAMETERS, kv("query", sql), kv("parameters", getParameters(context)));
            } else {
                log.info(TEMPLATE, kv("query", sql));
            }
        }
    }

    private static String chompWhitespace(final String query) {
        return query.trim().replaceAll("\\s+", " ");
    }


    @SneakyThrows({SecurityException.class, NoSuchFieldException.class, IllegalAccessException.class})
    private static Object getParameters(final StatementContext context) {
        final var bindingClass = Binding.class;
        final var field = bindingClass.getDeclaredField("named");
        field.setAccessible(true);

        return field.get(context.getBinding());
    }
}

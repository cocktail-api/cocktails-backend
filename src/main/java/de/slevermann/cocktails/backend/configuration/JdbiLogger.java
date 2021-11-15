package de.slevermann.cocktails.backend.configuration;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.argument.MapArguments;
import org.jdbi.v3.core.argument.NamedArgumentFinder;
import org.jdbi.v3.core.argument.internal.ObjectPropertyNamedArgumentFinder;
import org.jdbi.v3.core.argument.internal.PojoPropertyArguments;
import org.jdbi.v3.core.statement.Binding;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * This class is an unholy abomination that really should not exist. The performance drag of evaluating all the
 * arguments is probably noticeable even on a slow scale. Nice for debugging, though!
 */
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
        // First, try to extract the named arguments
        final var bindingClass = Binding.class;
        var field = bindingClass.getDeclaredField("named");
        field.setAccessible(true);

        @SuppressWarnings("unchecked") final var params = (Map<String, Object>) field.get(context.getBinding());

        if (params.isEmpty()) {
            // No named arguments available, try positionals:
            field = bindingClass.getDeclaredField("positionals");
            field.setAccessible(true);

            @SuppressWarnings("unchecked") final var positionalParams = (Map<Integer, Object>) field.get(context.getBinding());
            if (positionalParams.isEmpty()) {
                // No positional parameters either, try named argument finders
                field = bindingClass.getDeclaredField("namedArgumentFinder");
                field.setAccessible(true);
                @SuppressWarnings("unchecked") final var finders = (List<NamedArgumentFinder>) field.get(context.getBinding());
                final var boundProperties = new ArrayList<>();
                for (final var finder : finders) {
                    @SuppressWarnings("deprecation") final var mapArgs = finder instanceof MapArguments;
                    if (finder instanceof PojoPropertyArguments || mapArgs) {
                        final var args = new HashMap<String, Object>();
                        for (final var name : finder.getNames()) {
                            args.put(name, finder.find(name, context).orElse(null));
                        }
                        boundProperties.add(args);
                    } else if (finder instanceof ObjectPropertyNamedArgumentFinder f) {
                        final var objField = ObjectPropertyNamedArgumentFinder.class.getDeclaredField("obj");
                        objField.setAccessible(true);
                        final var obj = objField.get(f);
                        final var prefixField = ObjectPropertyNamedArgumentFinder.class.getDeclaredField("prefix");
                        prefixField.setAccessible(true);
                        final var prefix = (String) prefixField.get(f);
                        boundProperties.add(new PrefixedObject(obj, prefix));
                    }
                }
                return boundProperties;
            } else {
                return positionalParams;
            }
        } else {
            return params;
        }
    }

    private static record PrefixedObject(@NonNull Object obj, String prefix) {
    }
}

package de.slevermann.cocktails.backend.configuration.jdbi;

import de.slevermann.cocktails.backend.dao.CocktailDao;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.dao.UserDao;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.enums.EnumStrategy;
import org.jdbi.v3.core.enums.Enums;
import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.EnumMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.TimestampedConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.time.ZoneOffset;
import java.util.List;

@Configuration
@EnableConfigurationProperties(JdbiConfigurationProperties.class)
public class JdbiConfiguration {

    @Bean
    public SqlObjectPlugin plugin() {
        return new SqlObjectPlugin();
    }

    @Bean
    public PostgresPlugin postgresPlugin() {
        return new PostgresPlugin();
    }

    @Bean
    public ColumnMapper<DbUnit> unitColumnMapper() {
        return EnumMapper.byName(DbUnit.class);
    }

    @Bean
    public Jdbi jdbi(final DataSource ds,
                     final List<JdbiPlugin> jdbiPlugins,
                     final List<RowMapper<?>> rowMappers,
                     final JdbiConfigurationProperties config,
                     final ColumnMapper<DbUnit> unitColumnMapper,
                     final JdbiLogger jdbiLogger) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
        Jdbi jdbi = Jdbi.create(proxy);
        jdbiPlugins.forEach(jdbi::installPlugin);
        rowMappers.forEach(jdbi::registerRowMapper);
        jdbi.getConfig().get(Enums.class).setEnumStrategy(EnumStrategy.BY_NAME);
        jdbi.registerColumnMapper(new GenericType<>() {
        }, unitColumnMapper);
        if (config.isEnabled()) {
            jdbi.setSqlLogger(jdbiLogger);
        }
        jdbi.getConfig().get(TimestampedConfig.class).setTimezone(ZoneOffset.UTC);
        return jdbi;
    }

    @Bean
    public IngredientDao ingredientDao(final Jdbi jdbi) {
        return jdbi.onDemand(IngredientDao.class);
    }

    @Bean
    public IngredientTypeDao typeDao(final Jdbi jdbi) {
        return jdbi.onDemand(IngredientTypeDao.class);
    }

    @Bean
    public CocktailDao cocktailDao(final Jdbi jdbi) {
        return jdbi.onDemand(CocktailDao.class);
    }

    @Bean
    public UserDao userDao(final Jdbi jdbi) {
        return jdbi.onDemand(UserDao.class);
    }
}

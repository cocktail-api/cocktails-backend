package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.UUID;

@UseClasspathSqlLocator
public interface CocktailDao {

    @SqlQuery
    @Timed(value = "cocktails.getAll",
            description = "Performance of cocktail list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbCocktail> getAll(@Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

    @SqlQuery
    @Timed(value = "cocktails.findById",
            description = "Performance of cocktail list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbCocktail findById(@Bind("uuid") final UUID uuid);

}

package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCreateCocktail;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

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
    @Timed(value = "cocktails.getById",
            description = "Performance of cocktail fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbCocktail getById(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "cocktails.count",
            description = "Performance of cocktail counting",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long count();

    @GetGeneratedKeys
    @SqlUpdate
    @Timed(value = "cocktails.create",
            description = "Performance of cocktail creation",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbCocktail create(@BindMethods final DbCreateCocktail cocktail);

    @SqlUpdate
    @Timed(value = "cocktails.delete",
            description = "Performance of cocktail deletion",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    int delete(@Bind("uuid") final UUID uuid);

    @GetGeneratedKeys
    @SqlUpdate
    @Timed(value = "cocktails.update",
            description = "Performance of cocktail update",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbCocktail update(@Bind("uuid") final UUID uuid, @BindMethods final DbCreateCocktail cocktail);

}

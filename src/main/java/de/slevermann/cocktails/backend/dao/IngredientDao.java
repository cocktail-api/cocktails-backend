package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.UUID;

@UseClasspathSqlLocator
public interface IngredientDao {

    @SqlQuery
    @Timed(value = "ingredients.getAll",
            description = "Performance of ingredient list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbIngredient> getAll(@Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

    @SqlQuery
    @Timed(value = "ingredients.findByCocktail",
            description = "Performance of ingredient fetching by cocktail",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbCocktailIngredient> findByCocktail(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "ingredients.count",
            description = "Performance of ingredient counting",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long count();
}

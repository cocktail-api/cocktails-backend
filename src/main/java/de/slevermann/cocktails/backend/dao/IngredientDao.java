package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

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

    @SqlQuery
    @Timed(value = "ingredients.create",
            description = "Performance of ingredient creation",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbIngredient create(@BindMethods final DbCreateIngredient ingredient);

    @SqlUpdate
    @Timed(value = "ingredients.delete",
            description = "Performance of ingredient creation",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    int delete(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "ingredients.get",
            description = "Performance of ingredient fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbIngredient getById(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "ingredients.usedByCocktailsCount",
            description = "Performance of counting cocktails an ingredient is used by",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long usedByCount(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "ingredients.shelfCount",
            description = "Performance of counting users that have the ingredient",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long shelfCount(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "ingredients.update",
            description = "Performance of updating ingredients",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    @Timestamped
    DbIngredient update(@Bind("uuid") final UUID uuid,
                        @BindMethods final DbCreateIngredient ingredient);
}

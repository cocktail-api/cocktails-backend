package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@UseClasspathSqlLocator
public interface IngredientDao {

    @SqlQuery
    @Timed(value = "ingredients.getAll",
            description = "Performance of ingredient list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbIngredient> getAll(@Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

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

    @SqlQuery
    @Timed(value = "ingredients.findByType",
            description = "Performance of fetching ingredients by type",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbIngredient> findByType(@Bind("type") final UUID type,
                                  @Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

    @SqlQuery
    @Timed(value = "ingredients.findIngredients",
            description = "Performance of fetching ingredients by UUID",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    Set<UUID> findIngredients(@BindList("ingredients") final Set<UUID> ingredients);

    @SqlQuery
    @Timed(value = "ingredients.countByType",
            description = "Performance of ingredient counting by type",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long countByType(@Bind("type") final UUID uuid);
}

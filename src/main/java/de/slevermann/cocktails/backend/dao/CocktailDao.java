package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktail;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateInstruction;
import de.slevermann.cocktails.backend.model.db.DbInstruction;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Set;
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
    @Timestamped
    DbCocktail update(@Bind("uuid") final UUID uuid, @BindMethods final DbCreateCocktail cocktail);


    @SqlQuery
    @Timed(value = "cocktails.getIngredients",
            description = "Performance of cocktail ingredient fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbCocktailIngredient> getIngredients(@Bind("uuid") final UUID uuid);

    @SqlBatch
    @Timed(value = "cocktails.addIngredients",
            description = "Performance of cocktail ingredient adding",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    @Timestamped
    void addIngredients(@Bind("cocktail") final UUID cocktail, @BindMethods final List<DbCreateCocktailIngredient> ingredients);

    default void addIngredient(final UUID cocktail, final DbCreateCocktailIngredient ingredient) {
        addIngredients(cocktail, List.of(ingredient));
    }

    @SqlBatch
    @Timed(value = "cocktails.removeIngredients",
            description = "Performance of cocktail ingredient removal",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    void removeIngredients(@Bind("cocktail") final UUID cocktail, @Bind("ingredient") final Set<UUID> ingredients);

    default void removeIngredient(final UUID cocktail, final UUID ingredient) {
        removeIngredients(cocktail, Set.of(ingredient));
    }

    @GetGeneratedKeys
    @SqlBatch
    @Timed(value = "cocktails.addInstructions",
            description = "Performance of cocktail instruction creation",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    @Timestamped
    List<DbInstruction> addInstructions(@Bind("cocktail") final UUID cocktail, @BindMethods final Set<DbCreateInstruction> instructions);

    @SqlQuery
    @Timed(value = "cocktails.getInstructions",
            description = "Performance of cocktail instruction fetch",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbInstruction> getInstructions(@Bind("cocktail") final UUID cocktail);

    @SqlUpdate
    @Timed(value = "cocktails.clearInstructions",
            description = "Performance of cocktail instruction clearing",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    int clearInstructions(@Bind("cocktail") final UUID cocktail);

    @SqlQuery
    @Timed(value = "cocktails.getByIngredient",
            description = "Performance of cocktail fetch by ingredient",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbCocktail> findByIngredient(@Bind("offset") final int offset,
                                      @Bind("pageSize") final int pageSize,
                                      @Bind("uuid") final UUID uuid);
}

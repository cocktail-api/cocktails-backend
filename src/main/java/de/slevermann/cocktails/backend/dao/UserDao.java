package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbUser;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@UseClasspathSqlLocator
public interface UserDao {

    @SqlQuery
    @Timed(value = "users.count",
            description = "Performance of user counting",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long count();

    @GetGeneratedKeys
    @SqlUpdate
    @Timed(value = "users.create",
            description = "Performance of user counting",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbUser create(@Bind("nick") final String nick);

    default DbUser create() {
        return create(null);
    }

    @GetGeneratedKeys
    @SqlUpdate
    @Timed(value = "users.update",
            description = "Performance of user update",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbUser update(@Bind("uuid") final UUID uuid, @Bind("nick") final String nick);

    @SqlUpdate
    @Timed(value = "users.delete",
            description = "Performance of user deletion",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    int delete(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "users.getByNick",
            description = "Performance of user fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbUser getByNick(@Bind("nick") final String nick);

    @SqlBatch
    @Timed(value = "users.addShelf",
            description = "Performance of adding ingredients to a shelf",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    void addToShelf(@Bind("user") final UUID user,
                    @Bind("ingredient") final Set<UUID> ingredients);

    default void addToShelf(final UUID user, final UUID ingredient) {
        addToShelf(user, Set.of(ingredient));
    }

    @SqlBatch
    @Timed(value = "users.removeShelf",
            description = "Performance of removing ingredients from a shelf",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    void removeFromShelf(@Bind("user") final UUID user,
                         @Bind("ingredient") final Set<UUID> ingredients);

    default void removeFromShelf(final UUID user, final UUID ingredient) {
        removeFromShelf(user, Set.of(ingredient));
    }

    @SqlQuery
    @Timed(value = "users.shelf",
            description = "Performance of getting a user's shelf",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    Set<DbIngredient> getShelf(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "users.getAll",
            description = "Performance of user list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbUser> getAll(@Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

}

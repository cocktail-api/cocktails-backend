package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

@UseClasspathSqlLocator
public interface IngredientTypeDao {

    @SqlQuery
    @Timed(value = "types.getAll",
            description = "Performance of ingredient type list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbIngredientType> getAll(@Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

    @SqlQuery
    @Timed(value = "types.count",
            description = "Performance of ingredient type counting",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long count();

    @SqlQuery
    @Timed(value = "types.getById",
            description = "Performance of ingredient type counting",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbIngredientType getById(@Bind("uuid") final UUID uuid);

    @GetGeneratedKeys
    @SqlUpdate
    @Timed(value = "types.create",
            description = "Performance of ingredient type creation",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbIngredientType create(@Bind("name") final String name);

    @SqlUpdate
    @Timed(value = "types.delete",
            description = "Performance of ingredient type deletion",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    int delete(@Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "types.usedByCount",
            description = "Performance of ingredient type use count",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    long usedByCount(@Bind("uuid") final UUID uuid);

    @GetGeneratedKeys
    @SqlUpdate
    @Timed(value = "types.update",
            description = "Performance of ingredient type update",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    @Timestamped
    DbIngredientType update(@Bind("uuid") final UUID uuid, @Bind("name") final String name);

    @SqlQuery
    @Timed(value = "types.findByName",
            description = "Performance of ingredient type name lookup",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbIngredientType findByName(@Bind("name") final String name);

    @SqlQuery
    @Timed(value = "types.findByNameAndNot",
            description = "Performance of ingredient type name and != id lookup",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    DbIngredientType findByNameAndNotId(@Bind("name") final String name, @Bind("uuid") final UUID uuid);

    @SqlQuery
    @Timed(value = "types.search",
            description = "Performance of ingredient type search",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbIngredientType> search(@Bind("searchTerm") final String searchTerm,
                                  @Bind("offset") final int offset,
                                  @Bind("pageSize") final int pageSize);
}

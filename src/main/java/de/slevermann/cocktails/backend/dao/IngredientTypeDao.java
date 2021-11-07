package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import io.micrometer.core.annotation.Timed;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

@UseClasspathSqlLocator
public interface IngredientTypeDao {

    @SqlQuery
    @Timed(value = "types.getAll",
            description = "Performance of ingredient type list fetching",
            percentiles = {0.99, 0.95, 0.9, 0.5})
    List<DbIngredientType> getAll(@Bind("offset") final int offset, @Bind("pageSize") final int pageSize);

}

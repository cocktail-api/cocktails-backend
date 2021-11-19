package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@JdbiRowMapper
@RequiredArgsConstructor
public class CocktailIngredientMapper implements RowMapper<DbCocktailIngredient> {

    private final IngredientMapper ingredientMapper;

    private final ColumnMapper<DbUnit> unitColumnMapper;

    @Override
    public DbCocktailIngredient map(ResultSet rs, StatementContext ctx) throws SQLException {
        final var ingredient = ingredientMapper.map(rs, ctx);
        final var amount = rs.getBigDecimal("amount");
        return new DbCocktailIngredient(ingredient,
                amount == null ? null : amount.doubleValue(),
                unitColumnMapper.map(rs, "unit", ctx),
                rs.getBoolean("garnish"),
                rs.getBoolean("optional"),
                rs.getObject("ci_created", OffsetDateTime.class),
                rs.getObject("ci_modified", OffsetDateTime.class));
    }
}

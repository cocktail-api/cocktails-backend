package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbIngredient;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Mapper
@RequiredArgsConstructor
public class IngredientMapper implements RowMapper<DbIngredient> {

    private final IngredientTypeMapper ingredientTypeMapper;

    @Override
    public DbIngredient map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new DbIngredient(rs.getObject("ingredient_uuid", UUID.class),
                ingredientTypeMapper.map(rs, ctx),
                rs.getString("ingredient_name"),
                rs.getString("ingredient_description"));
    }
}

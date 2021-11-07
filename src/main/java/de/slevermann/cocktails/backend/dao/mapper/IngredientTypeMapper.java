package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Mapper
public class IngredientTypeMapper implements RowMapper<DbIngredientType> {

    @Override
    public DbIngredientType map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new DbIngredientType(rs.getObject("type_uuid", UUID.class),
                rs.getString("type_name"));
    }
}

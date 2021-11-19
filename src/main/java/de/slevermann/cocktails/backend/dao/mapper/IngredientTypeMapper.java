package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

@JdbiRowMapper
public class IngredientTypeMapper implements RowMapper<DbIngredientType> {

    @Override
    public DbIngredientType map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new DbIngredientType(rs.getObject("type_uuid", UUID.class),
                rs.getString("type_name"),
                rs.getObject("type_created", OffsetDateTime.class),
                rs.getObject("type_modified", OffsetDateTime.class));
    }
}

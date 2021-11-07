package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Mapper
public class CocktailMapper implements RowMapper<DbCocktail> {

    @Override
    public DbCocktail map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new DbCocktail(rs.getObject("cocktail_uuid", UUID.class),
                rs.getString("cocktail_name"),
                rs.getString("cocktail_description"));
    }
}

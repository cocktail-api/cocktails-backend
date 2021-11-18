package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbUser;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@JdbiRowMapper
public class UserMapper implements RowMapper<DbUser> {

    @Override
    public DbUser map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new DbUser(rs.getObject("user_uuid", UUID.class),
                rs.getString("user_nick"));
    }
}

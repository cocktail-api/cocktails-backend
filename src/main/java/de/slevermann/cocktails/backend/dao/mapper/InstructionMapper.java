package de.slevermann.cocktails.backend.dao.mapper;

import de.slevermann.cocktails.backend.model.db.DbInstruction;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@JdbiRowMapper
public class InstructionMapper implements RowMapper<DbInstruction> {

    @Override
    public DbInstruction map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new DbInstruction(rs.getString("instruction_text"),
                rs.getInt("instruction_number"),
                rs.getObject("instruction_created", OffsetDateTime.class),
                rs.getObject("instruction_modified", OffsetDateTime.class));
    }
}

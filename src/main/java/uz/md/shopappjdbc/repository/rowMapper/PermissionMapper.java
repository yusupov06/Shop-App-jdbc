package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionMapper implements RowMapper<PermissionEnum> {
    @Override
    public PermissionEnum mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PermissionEnum.valueOf(rs.getString("permission"));
    }
}

package uz.md.shopappjdbc.repository.rowMapper;

import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.repository.RepositoryUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        role.setActive(rs.getBoolean("active"));
        role.setAddedAt(RepositoryUtil.getLocalDateTimeFromString(rs.getString("added_at")));
        role.setDeleted(rs.getBoolean("deleted"));
        return role;
    }
}

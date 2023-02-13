package uz.md.shopappjdbc.repository.rowMapper;

import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.repository.RepositoryUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPassword(rs.getString("password"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setActive(rs.getBoolean("active"));
        user.setAddedAt(RepositoryUtil
                .getLocalDateTimeFromString(rs.getString("added_at")));
        user.setRole(new Role(rs.getInt("role_id")));
        return user;
    }
}

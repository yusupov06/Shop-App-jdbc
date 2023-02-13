package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Address;
import uz.md.shopappjdbc.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AddressMapper implements RowMapper<Address> {
    @Override
    public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
        Address address = new Address();
        address.setId(rs.getLong("id"));
        address.setCity(rs.getString("city"));
        address.setStreet(rs.getString("street"));
        address.setHouseNumber(rs.getInt("house_number"));
        address.setDeleted(rs.getBoolean("deleted"));
        address.setUser(new User(UUID.fromString(rs.getString("user_id"))));
        return address;
    }
}

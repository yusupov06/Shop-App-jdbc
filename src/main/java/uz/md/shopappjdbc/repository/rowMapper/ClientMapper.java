package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Client;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientMapper implements RowMapper<Client> {
    @Override
    public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
        Client client = new Client();
        client.setPhoneNumber(rs.getString("phone_number"));
        client.setUsername(rs.getString("username"));
        return client;
    }
}

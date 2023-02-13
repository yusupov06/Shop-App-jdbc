package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Address;
import uz.md.shopappjdbc.domain.Order;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.domain.enums.OrderStatus;
import uz.md.shopappjdbc.repository.RepositoryUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setDeleted(rs.getBoolean("deleted"));
        order.setActive(rs.getBoolean("active"));
        order.setAddedAt(RepositoryUtil.getLocalDateTimeFromString(rs.getString("added_at")));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setOverallPrice(rs.getDouble("overall_price"));
        order.setAddress(new Address(rs.getLong("address_id")));
        order.setUser(new User(UUID.fromString(rs.getString("user_id"))));
        return order;
    }
}

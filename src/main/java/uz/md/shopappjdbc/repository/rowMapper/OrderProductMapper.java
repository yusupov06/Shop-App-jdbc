package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Order;
import uz.md.shopappjdbc.domain.OrderProduct;
import uz.md.shopappjdbc.domain.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderProductMapper implements RowMapper<OrderProduct> {
    @Override
    public OrderProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(rs.getLong("id"));
        orderProduct.setDeleted(rs.getBoolean("deleted"));
        orderProduct.setPrice(rs.getDouble("price"));
        orderProduct.setQuantity(rs.getInt("quantity"));
        orderProduct.setProduct(new Product(rs.getLong("id")));
        orderProduct.setOrder(new Order(rs.getLong("id")));
        return orderProduct;
    }
}

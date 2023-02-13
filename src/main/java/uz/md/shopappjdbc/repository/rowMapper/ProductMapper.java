package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.repository.RepositoryUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ProductMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setDescription(rs.getString("description"));
        product.setActive(rs.getBoolean("active"));
        product.setDeleted(rs.getBoolean("deleted"));
        product.setAddedAt(RepositoryUtil.getLocalDateTimeFromString(rs.getString("added_at")));
        product.setCategory(new Category(rs.getLong("category_id")));
        return product;
    }
}

package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.repository.RepositoryUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CategoryMapper implements RowMapper<Category> {
    @Override
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setDeleted(rs.getBoolean("deleted"));
        category.setActive(rs.getBoolean("active"));
        category.setAddedAt(RepositoryUtil.getLocalDateTimeFromString(rs.getString("added_at")));
        return category;
    }
}

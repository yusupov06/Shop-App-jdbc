package uz.md.shopappjdbc.repository.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import uz.md.shopappjdbc.domain.AccessKey;
import uz.md.shopappjdbc.domain.Client;
import uz.md.shopappjdbc.repository.RepositoryUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AccessKeyMapper implements RowMapper<AccessKey> {

    @Override
    public AccessKey mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccessKey accessKey = new AccessKey();
        accessKey.setId(rs.getLong("id"));
        accessKey.setAccess(rs.getString("access"));
        accessKey.setDeleted(rs.getBoolean("deleted"));
        accessKey.setClient(new Client(rs.getLong("client_id")));
        LocalDateTime validTill = RepositoryUtil.getLocalDateTimeFromString(rs.getString("valid_till"));
        accessKey.setValidTill(validTill);
        return accessKey;
    }
}

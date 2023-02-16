package uz.md.shopappjdbc.repository.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.AccessKey;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.AccessKeyRepository;
import uz.md.shopappjdbc.repository.contract.ClientRepository;
import uz.md.shopappjdbc.repository.rowMapper.AccessKeyMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AccessKeyRepositoryImpl implements AccessKeyRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ClientRepository clientRepository;

    public AccessKeyRepositoryImpl(JdbcTemplate jdbcTemplate,
                                   @Lazy ClientRepository clientRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.clientRepository = clientRepository;
    }

    @Override
    public AccessKey save(AccessKey entity) {
        Assert.notNull(entity, "entity must not be null");

        if (entity.getClient() == null)
            throw new SQLException("CLIENT_CANNOT_BE_NULL");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update access_key set access = ?, deleted = ?, valid_till = ?, client_id=? where id = ?",
                    entity.getAccess(),
                    entity.isDeleted(),
                    entity.getValidTill(),
                    entity.getClient().getId(),
                    entity.getId());
            return entity = findById(entity.getId()).orElse(null);
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            AccessKey finalEntity = entity;
            jdbcTemplate.update(con -> {
                        PreparedStatement ps = con.prepareStatement(
                                "insert into access_key(access, deleted, valid_till, client_id) " +
                                        "values (?,?,?,?)", new String[]{"id"});
                        ps.setString(1, finalEntity.getAccess());
                        ps.setBoolean(2, finalEntity.isDeleted());
                        ps.setDate(3, Date.valueOf(finalEntity.getValidTill().toLocalDate()));
                        return ps;
                    },
                    keyHolder
            );
            if (keyHolder.getKey() == null)
                throw new SQLException("CANNOT_SAVE_ENTITY");

            return entity = findById(keyHolder.getKey().longValue()).orElse(null);
        }
    }

    @Override
    public List<AccessKey> saveAll(List<AccessKey> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<AccessKey> result = new ArrayList<>();
        for (AccessKey entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<AccessKey> findById(Long id) {
        AccessKey accessKey = jdbcTemplate.queryForObject(
                "select u.id from access_key u where (u.deleted = false) and u.id = ?",
                new AccessKeyMapper(), id);

        return Optional.ofNullable(accessKey);
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "id must not be null");
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count()>0 then true else false end from access_key u where u.id = ? and (u.deleted = false)",
                Boolean.class, id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<AccessKey> findAll() {
        return jdbcTemplate.query(
                "select u.id from access_key u where (u.deleted = false)",
                new AccessKeyMapper());
    }

    private void setClient(List<AccessKey> query) {
        for (AccessKey access_key : query) {
            access_key.setClient(clientRepository
                    .findById(access_key.getClient().getId())
                    .orElseThrow(() -> new SQLException("CLIENT_NOT_FOUND")));
        }

    }

    @Override
    public List<AccessKey> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "idList must not be null");
        String ids = RepositoryUtil.getAsString(idList);

        return jdbcTemplate.query(
                "select id from access_key where (deleted = false) and id in ?",
                new AccessKeyMapper(), ids);

    }

    @Override
    public long count() {
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                "select count(id) from access_key where deleted = false",
                Long.class));
    }

    @Override
    public void deleteById(Long uuid) {
        Assert.notNull(uuid, "uuid must not be null");
        jdbcTemplate.update("update access_key set deleted = true where id = ?", uuid);
    }

    @Override
    public void delete(AccessKey entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> uuids) {
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("update access_key set deleted = true where id = ?", ids);
    }

    @Override
    public void deleteAll(Iterable<? extends AccessKey> entities) {
        for (AccessKey entity : entities) {
            deleteById(entity.getId());
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from access_key");
    }

    @Override
    public Optional<AccessKey> findByAccess(String access) {
        Assert.notNull(access, "access must not be null");
        AccessKey accessKey = jdbcTemplate.queryForObject(
                "select * from access_key where access = ? and (deleted = false)",
                AccessKey.class, access);
        if (accessKey == null)
            return Optional.empty();

        accessKey.setClient(clientRepository
                .findById(accessKey.getClient().getId())
                .orElse(null));

        return Optional.of(accessKey);
    }

    @Override
    public List<AccessKey> findAllByClientId(Long id) {
        Assert.notNull(id, "id must not be null");
        return jdbcTemplate.query(
                "select * from access_key where client_id = ? and (deleted = false)",
                new AccessKeyMapper(), id);
    }
}

package uz.md.shopappjdbc.repository.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Address;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.AddressRepository;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.repository.rowMapper.AddressMapper;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class AddressRepositoryImpl implements AddressRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public AddressRepositoryImpl(JdbcTemplate jdbcTemplate,
                                 @Lazy UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public Address save(Address entity) {
        Assert.notNull(entity, "Entity must not be null");

        if (entity.getUser() == null)
            throw new SQLException("USER_CANNOT_BE_NULL");

        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update address set city = ?, house_number = ?, street = ?, user_id = ? where id = ?",
                    entity.getCity(),
                    entity.getHouseNumber(),
                    entity.getStreet(),
                    entity.getUser().getId(),
                    entity.getId());
            return entity = findById(entity.getId()).orElse(null);
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            Address finalEntity = entity;
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(
                        "insert into address(city, deleted, house_number, street, user_id) " +
                                "values (?,?,?,?,?)", new String[]{"id"});

                statement.setString(1, finalEntity.getCity());
                statement.setBoolean(2, finalEntity.isDeleted());
                statement.setInt(3, finalEntity.getHouseNumber());
                statement.setString(4, finalEntity.getStreet());
                statement.setObject(5, finalEntity.getUser().getId());
                return statement;
            }, keyHolder);
            return entity = findById(Objects
                    .requireNonNull(keyHolder.getKey())
                    .longValue())
                    .orElse(null);
        }

    }

    private Optional<Address> findTheLastSaved() {

        Address address = jdbcTemplate
                .queryForObject("select * from address where id = (select max(id) from address)",
                        new AddressMapper());
        if (address != null) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    @Override
    public List<Address> saveAll(List<Address> entities) {
        Assert.notNull(entities, "Entities must not be null");
        List<Address> result = new ArrayList<>();
        for (Address entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<Address> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        Address address = jdbcTemplate.queryForObject(
                "select u.id from address u where deleted = false and u.id = ?",
                new AddressMapper(), id);

        if (address != null) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count()>0 then true else false end from address u where deleted = false and u.id = ?",
                Boolean.class,
                id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<Address> findAll() {

        return jdbcTemplate.query(
                "select * from address  where deleted = false",
                new AddressMapper());
    }

    private void setUser(List<Address> query) {
        for (Address address : query) {
            address.setUser(userRepository
                    .findById(address.getUser().getId())
                    .orElse(null));
        }

    }

    @Override
    public List<Address> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "ids must not be null");
        String ids = RepositoryUtil.getAsString(idList);

        return jdbcTemplate.query(
                "select * from address where deleted = false and id in ?",
                new AddressMapper(),
                ids);

    }

    @Override
    public long count() {
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                "select count(id) from address where deleted = false",
                Long.class));
    }

    @Override
    public void deleteById(Long uuid) {
        Assert.notNull(uuid, "id must not be null");
        jdbcTemplate.update("update address set deleted = true where id = ?", uuid);
    }

    @Override
    public void delete(Address entity) {
        Assert.notNull(entity, "entity must not be null");
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> uuids) {
        Assert.notNull(uuids, "ids must not be null");
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("update address set deleted = true where id in ?", ids);
    }

    @Override
    public void deleteAll(Iterable<? extends Address> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Long> ids = new ArrayList<>();
        entities.forEach(product -> ids.add(product.getId()));
        deleteAllById(ids);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from address");
    }


    @Override
    public Optional<Address> findByIdAndUserId(Long id, UUID user_id) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(user_id, "userId must not be null");
        Address address = jdbcTemplate.queryForObject(
                "select u.id from address u where deleted = false and u.id = ? and u.user_id = ?",
                new AddressMapper(), id, user_id);

        if (address != null) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    @Override
    public List<Address> findAllByUserId(UUID userId) {
        List<Address> addresses = jdbcTemplate.query(
                "select * from address where deleted = false and user_id = ?",
                new AddressMapper(), userId);
        return addresses;
    }
}

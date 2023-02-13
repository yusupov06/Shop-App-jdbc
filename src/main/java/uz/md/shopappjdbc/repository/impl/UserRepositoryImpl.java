package uz.md.shopappjdbc.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.AddressRepository;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.repository.rowMapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        Assert.notNull(phoneNumber, "Phone number cannot be null");
        User user = jdbcTemplate.queryForObject(
                "select * from users where phone_number = ? and (deleted = false)",
                new UserMapper(), phoneNumber);

        if (user == null) return Optional.empty();
        if (user.getRole() == null)
            throw new SQLException("ROLE_CANNOT_BE_NULL");
        setRole(user);
        setAddresses(user);
        return Optional.of(user);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Assert.notNull(phoneNumber, "Phone number cannot be null");
        Long aLong = jdbcTemplate.queryForObject(
                "select count(id) from users u where (u.deleted = false) and u.phone_number = '" + phoneNumber + "'",
                Long.class);
        return aLong != null && aLong != 0;
    }

    @Override
    public User save(User entity) {
        Assert.notNull(entity, "Entity must not be null");
        Role role;

        if (entity.getRole() == null)
            throw new SQLException("ROLE_CANNOT_BE_NULL");

        if (entity.getRole().getId() == null)
            role = roleRepository.save(entity.getRole());
        else
            role = entity.getRole();
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update  users set active = ?, added_at = ?, deleted= ?, enabled =?, first_name= ?, last_name=?, password=?, phone_number=?, role_id=? where id = ?",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.isEnabled(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getPassword(),
                    entity.getPhoneNumber(),
                    role.getId(),
                    entity.getId());
        } else {
            jdbcTemplate.update(
                    "insert into users(active, added_at, deleted, enabled, first_name, last_name, password, phone_number, role_id) " +
                            "values (?,?,?,?,?,?,?,?,?)",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.isEnabled(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getPassword(),
                    entity.getPhoneNumber(),
                    role.getId()
            );
        }
        entity = findByPhoneNumber(entity.getPhoneNumber()).orElse(null);
        return entity;
    }


    @Override
    public List<User> saveAll(List<User> entities) {
        Assert.notNull(entities, "Entities must not be null");
        List<User> result = new ArrayList<>();
        for (User entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<User> findById(UUID uuid) {
        Assert.notNull(uuid, "Id must not be null");
        User user = jdbcTemplate.queryForObject(
                "select * from users where (deleted = false) and id = ?",
                new UserMapper(), uuid);

        if (user != null) {
            setRole(user);
            setAddresses(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        Assert.notNull(uuid, "Id cannot be null");
        Long aLong = jdbcTemplate.queryForObject(
                "select count(id) from users where (deleted = false) and id = ?",
                Long.class, uuid);
        return aLong != null && aLong != 0;
    }

    @Override
    public List<User> findAll() {
        List<User> query = jdbcTemplate.query(
                """
                        select * from users u
                            where (deleted = false)
                        """,
                new UserMapper());
        setRoles(query);
        setAddresses(query);
        return query;
    }

    private void setAddresses(List<User> query) {
        for (User user : query) {
            user.setAddresses(addressRepository
                    .findAllByUserId(user.getId()));
        }
    }

    private void setAddresses(User user) {
        user.setAddresses(addressRepository
                .findAllByUserId(user.getId()));
    }

    private void setRoles(List<User> query) {
        for (User user : query) {
            user.setRole(roleRepository
                    .findById(user.getRole().getId())
                    .orElseThrow(() -> new NotFoundException("ROLE_NOT_FOUND")));
        }
    }

    private void setRole(User user) {
        user.setRole(roleRepository
                .findById(user.getRole().getId())
                .orElseThrow(() -> new NotFoundException("ROLE_NOT_FOUND")));
    }

    @Override
    public List<User> findAllById(Iterable<UUID> uuids) {
        Assert.notNull(uuids, "Ids must not be null");
        String ids = RepositoryUtil.getAsString(uuids);
        List<User> query = jdbcTemplate.query(
                "select * from users where (deleted = false) and id in " + ids,
                new UserMapper());
        setRoles(query);
        setAddresses(query);
        return query;
    }

    @Override
    public long count() {
        Long aLong = jdbcTemplate.queryForObject("select count(id) from users",
                Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public void deleteById(UUID uuid) {
        jdbcTemplate.update("update users set deleted = true where id = ? ", uuid);
    }

    @Override
    public void delete(User entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("update users set deleted = true where id in ?", ids);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        Assert.notNull(entities, "Entities must not be null");
        List<UUID> idsList = new ArrayList<>();
        deleteAllById(idsList);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }
}

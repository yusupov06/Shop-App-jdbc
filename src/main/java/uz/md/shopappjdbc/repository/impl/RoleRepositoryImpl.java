package uz.md.shopappjdbc.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.repository.rowMapper.PermissionMapper;
import uz.md.shopappjdbc.repository.rowMapper.RoleMapper;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public Set<PermissionEnum> getPermissionsByRole(Integer roleId) {
        return new HashSet<>(jdbcTemplate.query(
                "select * from role_permission where role_id = ?",
                new PermissionMapper(), roleId));
    }

    @Override
    public Role save(Role entity) {
        Assert.notNull(entity, "entity must not be null");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update role set active =  ?, added_at =  ?, deleted =  ?, description =  ?, name =? where id = ?",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getDescription(),
                    entity.getName(),
                    entity.getId()
            );
        } else {
            jdbcTemplate.update(
                    "insert into role(active, added_at, deleted, description, name) " +
                            "values (?,?,?,?,?)",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getDescription(),
                    entity.getName()
            );
        }
        Integer id = findIdByName(entity.getName());
        if (id == null)
            throw new SQLException("ROLE_NOT_FOUND_WITH_NAME");

        saveAllPermissions(entity.getPermissions(), id);
        entity = findByName(entity.getName()).orElse(null);
    return  entity;
    }

    public void saveAllPermissions(Set<PermissionEnum> permissions, Integer roleId) {
        deleteAllPermissionsByRoleId(roleId);
        for (PermissionEnum permission : permissions) {
            savePermission(permission, roleId);
        }
    }

    private void deleteAllPermissionsByRoleId(Integer roleId) {
        Assert.notNull(roleId, "role id must not be null");
        jdbcTemplate.update("delete from role_permission where role_id = ?", roleId);
    }

    public void savePermission(PermissionEnum permission, Integer roleId) {
        jdbcTemplate.update("insert into role_permission(role_id, permission) " +
                "values (?,?)", roleId, permission.toString());
    }

    private boolean existsPermissionByIdAndName(Integer roleId, String permission) {
        Long aLong = jdbcTemplate.queryForObject(
                "select count(role_id) from role_permission where role_id = ? and permission = ? ",
                Long.class, roleId, permission);
        return aLong != null && aLong != 0;
    }

    public Integer findIdByName(String name) {
        return jdbcTemplate.queryForObject("select id from role where name = ?", Integer.class, name);
    }

    @Override
    public List<Role> saveAll(List<Role> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Role> result = new ArrayList<>();
        for (Role entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<Role> findById(Integer id) {
        Role role = jdbcTemplate.queryForObject(
                "select * from role r where (r.deleted = false) and r.id = ?",
                new RoleMapper(), id);

        if (role != null) {
            role.setPermissions(getPermissionsByRole(role.getId()));
            return Optional.of(role);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer id) {
        Long aLong = jdbcTemplate.queryForObject(
                "select count(id) from role where (deleted = false) and id = ?",
                Long.class, id);
        return aLong != null && aLong != 0;
    }

    @Override
    public List<Role> findAll() {

        List<Role> query = jdbcTemplate.query(
                "select * from role where (deleted = false)",
                new RoleMapper());

        setPermissions(query);
        return query;
    }

    @Override
    public List<Role> findAllById(Iterable<Integer> ids) {
        Assert.notNull(ids, "Ids must not be null");
        String idsList = RepositoryUtil.getAsString(ids);
        List<Role> query = jdbcTemplate.query(
                "select * from role where (deleted = false) and id in ?",
                new RoleMapper(), idsList);
        setPermissions(query);
        return query;
    }

    private void setPermissions(List<Role> query) {
        for (Role role : query) {
            role.setPermissions(getPermissionsByRole(role.getId()));
        }
    }

    @Override
    public long count() {
        Long aLong = jdbcTemplate.queryForObject(
                "select count(id) from users", Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update("update role set deleted = true where id = ?", id);
    }

    @Override
    public void delete(Role entity) {
        Assert.notNull(entity, "entity must not be null");
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        Assert.notNull(ids, "Ids must not be null");
        String idList = RepositoryUtil.getAsString(ids);
        jdbcTemplate.update("update role set deleted = true where id in ?", idList);
    }

    @Override
    public void deleteAll(Iterable<? extends Role> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Integer> ids = new ArrayList<>();
        entities.forEach(role -> ids.add(role.getId()));
        deleteAllById(ids);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        Long aLong = jdbcTemplate.queryForObject(
                "select count(id) from role where (deleted = false) and name = ?"
                , Long.class, name);
        return aLong != null && aLong != 0;
    }

    @Override
    public List<Role> findAllByIdIsNot(Integer id) {
        List<Role> query = jdbcTemplate.query(
                "select * from role where (deleted = false) and id <> ?",
                new RoleMapper(),
                id);
        setPermissions(query);
        return query;
    }

    @Override
    public Optional<Role> findByName(String name) {
        List<Role> query = jdbcTemplate.query(
                "select * from role where (deleted = false) and name = ?",
                new RoleMapper(),
                name);
        if (query.isEmpty()) return Optional.empty();
        Role role = query.get(0);
        role.setPermissions(getPermissionsByRole(role.getId()));
        return Optional.of(role);
    }
}

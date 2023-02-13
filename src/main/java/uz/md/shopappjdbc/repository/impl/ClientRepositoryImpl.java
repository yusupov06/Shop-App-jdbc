package uz.md.shopappjdbc.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Client;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.AccessKeyRepository;
import uz.md.shopappjdbc.repository.contract.ClientRepository;
import uz.md.shopappjdbc.repository.rowMapper.ClientMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AccessKeyRepository accessKeyRepository;

    @Override
    public Client save(Client entity) {
        Assert.notNull(entity, "entity must not be null");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update client set phone_number= ?, username = ? where id = ?",
                    entity.getPhoneNumber(),
                    entity.getUsername(),
                    entity.getId()
            );
        } else {
            jdbcTemplate.update(
                    "insert into client(phone_number, username) " +
                            "values (?,?)",
                    entity.getPhoneNumber(),
                    entity.getUsername()
            );
        }
        entity = findByPhoneNumber(entity.getPhoneNumber())
                .orElse(null);
    return entity;
    }

    private Optional<Client> findByPhoneNumber(String phoneNumber) {
        Assert.notNull(phoneNumber, "Phone number cannot be null");
        Client client = jdbcTemplate.queryForObject(
                "select u.id from client u where u.phone_number = " + phoneNumber,
                new ClientMapper());

        if (client != null) {
            client.setAccessKeys(accessKeyRepository
                    .findAllByClientId(client.getId()));
            return Optional.of(client);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> saveAll(List<Client> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Client> result = new ArrayList<>();
        for (Client entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<Client> findById(Long id) {
        Client client = jdbcTemplate.queryForObject(
                "select u.id from client u where u.id = ?",
                new ClientMapper(),
                id);

        if (client != null) {
            client.setAccessKeys(accessKeyRepository
                    .findAllByClientId(client.getId()));
            return Optional.of(client);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count()>0 then true else false end from client u where u.id = ?",
                Boolean.class,
                id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<Client> findAll() {

        List<Client> query = jdbcTemplate.query(
                "select * from client",
                new ClientMapper());
        setAccessKeys(query);
        return query;
    }

    private void setAccessKeys(List<Client> query) {
        for (Client client : query) {
            client.setAccessKeys(accessKeyRepository
                    .findAllByClientId(client.getId()));
        }
    }

    @Override
    public List<Client> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "ids must not be null");
        String ids = RepositoryUtil.getAsString(idList);
        List<Client> query = jdbcTemplate.query(
                "select * from client id in ",
                new ClientMapper(),
                ids);
        setAccessKeys(query);
        return query;

    }

    @Override
    public long count() {
        Long aLong = jdbcTemplate.queryForObject(
                "select count(id) from client",
                Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public void deleteById(Long uuid) {
        Assert.notNull(uuid, "Id must not be null");
        jdbcTemplate.update("delete from client where id = " + uuid);
    }

    @Override
    public void delete(Client entity) {
        Assert.notNull(entity, "entity must not be null");
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> uuids) {
        Assert.notNull(uuids, "Ids must not be null");
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("delete from client id in " + ids);
    }

    @Override
    public void deleteAll(Iterable<? extends Client> entities) {
        List<Long> ids = new ArrayList<>();
        entities.forEach(product -> ids.add(product.getId()));
        deleteAllById(ids);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from client");
    }


    @Override
    public boolean existsByUsername(String username) {
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count()>0 then true else false end from client u where u.username = ?",
                Boolean.class,
                username);
        return aBoolean != null && aBoolean;
    }
}

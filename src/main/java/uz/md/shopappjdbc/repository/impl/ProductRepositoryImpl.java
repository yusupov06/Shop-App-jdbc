package uz.md.shopappjdbc.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.ProductRepository;
import uz.md.shopappjdbc.repository.rowMapper.ProductMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product save(Product entity) {
        Assert.notNull(entity, "entity must not be null");
        if (entity.getCategory() == null || entity.getCategory().getId() == null)
            throw new SQLException("CATEGORY_CANNOT_BE_NULL");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update product set active = ?, added_at = ?, deleted = ?, description = ?, name = ?, price = ?, category_id = ? where id = ?",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getDescription(),
                    entity.getName(),
                    entity.getPrice(),
                    entity.getCategory().getId(),
                    entity.getId()
            );
        } else {
            jdbcTemplate.update(
                    "insert into product(active, added_at, deleted, description, name, price, category_id) " +
                            "values (?,?,?,?,?,?,?)",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getDescription(),
                    entity.getName(),
                    entity.getPrice(),
                    entity.getCategory().getId()
            );
        }
        entity = findByName(entity.getName()).orElse(null);
        return entity;
    }

    private Optional<Product> findByName(String name) {

        List<Product> products = jdbcTemplate.query(
                "select * from product where deleted = false and name = ?",
                new ProductMapper(),
                name);

        if (!products.isEmpty()) {
            Product product = products.get(0);
            return Optional.of(product);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> saveAll(List<Product> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Product> result = new ArrayList<>();
        for (Product entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<Product> findById(Long id) {
        List<Product> products = jdbcTemplate.query(
                "select * from product where deleted = false and id = ?",
                new ProductMapper(),
                id);

        if (!products.isEmpty()) {
            Product product = products.get(0);
            return Optional.of(product);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count(id)>0 then true else false end from product where deleted = false and id = ?",
                Boolean.class,
                id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<Product> findAll() {

        return jdbcTemplate.query(
                "select id from product where (deleted = false)",
                new ProductMapper());

    }

    @Override
    public List<Product> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "Ids must not be null");
        String ids = RepositoryUtil.getAsString(idList);

        return jdbcTemplate.query(
                "select * from product where (deleted = false) and id in ?",
                new ProductMapper(),
                ids);

    }

    @Override
    public long count() {
        Long aLong = jdbcTemplate.queryForObject("select count(id) from product", Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("update product set deleted = true where id = ?", id);
    }

    @Override
    public void delete(Product entity) {
        Assert.notNull(entity, "entity must not be null");
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> uuids) {
        Assert.notNull(uuids, "Ids must not be null");
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("update product set deleted = true where id in ?", ids);
    }

    @Override
    public void deleteAll(Iterable<? extends Product> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Long> ids = new ArrayList<>();
        entities.forEach(product -> ids.add(product.getId()));
        deleteAllById(ids);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from product");
    }

    @Override
    public boolean existsByName(String name) {
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count(c)> 0 then true else false end from product c where c.name = ?",
                Boolean.class,
                name);
        return aBoolean != null && aBoolean;
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, Long id) {
        Boolean aBoolean = jdbcTemplate.queryForObject("select case when count(c)> 0 then true else false end from " +
                        "product c where c.name = ? and id <> ?",
                Boolean.class,
                name, id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<Product> findAllByCategory_Id(Long category_id) {

        return jdbcTemplate.query(
                "select * from product where (deleted = false)" +
                        " and category_id = ?",
                new ProductMapper(),
                category_id);
    }


    @Override
    public Page<Product> findAll(PageRequest pageRequest) {
        List<Product> query = jdbcTemplate.query("select * from product where deleted = false limit ? offset ?",
                new ProductMapper(),
                pageRequest.getPageSize(),
                pageRequest.getPageNumber() * pageRequest.getPageSize());
        return new PageImpl<>(query, pageRequest, query.size());
    }

    @Override
    public Page<Product> execute(String query, Pageable pageable) {
        List<Product> query1 = jdbcTemplate.query(query, new ProductMapper());
        return new PageImpl<>(query1, pageable, query1.size());
    }


}

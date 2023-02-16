package uz.md.shopappjdbc.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.CategoryRepository;
import uz.md.shopappjdbc.repository.contract.ProductRepository;
import uz.md.shopappjdbc.repository.rowMapper.CategoryMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRepository productRepository;

    public CategoryRepositoryImpl(JdbcTemplate jdbcTemplate,
                                  ProductRepository productRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRepository = productRepository;
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findAllByCategory_Id(categoryId);
    }

    @Override
    public Category save(Category entity) {
        Assert.notNull(entity, "entity must not be null");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update category set active = ?, added_at = ?, deleted = ?, description = ?, name =? where id = ?",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getDescription(),
                    entity.getName(),
                    entity.getId());
        } else {
            jdbcTemplate.update(
                    "insert into category(active, added_at, deleted, description, name) " +
                            "values (?,?,?,?,?)",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getDescription(),
                    entity.getName());
        }

        entity = findByName(entity.getName()).orElse(null);
    return entity;
    }

    @Override
    public List<Category> saveAll(List<Category> entities) {
        Assert.notNull(entities, "entities must be non null");
        List<Category> result = new ArrayList<>();
        for (Category entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<Category> findById(Long id) {
        Assert.notNull(id, "Id must not be null");

        Category category = jdbcTemplate.queryForObject(
                "select * from category where deleted = false and id = ?",
                new CategoryMapper(), id);

        if (category != null) {
            category.setProducts(getProductsByCategory(category.getId()));
            return Optional.of(category);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long uuid) {
        Assert.notNull(uuid, "Id must not be null");
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count(id) > 0 then true else false end from category where deleted = false and id = ?",
                Boolean.class,
                uuid);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<Category> findAll() {

        List<Category> query = jdbcTemplate.query(
                """
                        select u.id from category u
                            where (u.deleted = false)
                        """,
                new CategoryMapper());
        setProducts(query);
        return query;
    }

    @Override
    public List<Category> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "Ids must not be null");
        String ids = RepositoryUtil.getAsString(idList);
        List<Category> query = jdbcTemplate.query(
                "select u.id from category u where (u.deleted = false) and u.id in ?",
                new CategoryMapper(), ids);

        setProducts(query);
        return query;

    }

    private void setProducts(List<Category> query) {
        for (Category category : query) {
            category.setProducts(getProductsByCategory(category.getId()));
        }
    }

    @Override
    public long count() {
        Long aLong = jdbcTemplate
                .queryForObject("select count(id) from category where deleted = false", Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public void deleteById(Long uuid) {
        Assert.notNull(uuid, "Id must not be null");
        jdbcTemplate.update("update category set deleted = true where id = ?", uuid);
    }

    @Override
    public void delete(Category entity) {
        Assert.notNull(entity, "entity must not be null");
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> uuids) {
        Assert.notNull(uuids, "Ids must not be null");
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("update category set deleted = true where id in ?", ids);
    }

    @Override
    public void deleteAll(Iterable<? extends Category> entities) {
        Assert.notNull(entities, "entities must not be null");
        for (Category entity : entities) {
            deleteById(entity.getId());
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from category");
    }

    @Override
    public boolean existsByName(String name) {
        Assert.notNull(name, "name must not be null");
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count(c)> 0 then true else false end from category c where deleted = false and c.name = ?",
                Boolean.class, name);
        return aBoolean != null && aBoolean;
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, Long id) {
        Assert.notNull(name, "name must not be null");
        Assert.notNull(id, "id must not be null");
        Boolean aBoolean = jdbcTemplate
                .queryForObject("select case when count(id)> 0 then true else false end from category where deleted = false and id <> ? and name = ?",
                        Boolean.class,
                        id, name);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<CategoryInfoDto> findAllForInfo() {
        return jdbcTemplate.query("select id,name,description from category where deleted = false"
                , (rs, rowNum) -> new CategoryInfoDto(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
    }

    @Override
    public Optional<Category> findByName(String name) {
        Assert.notNull(name, "name must not be null");
        return Optional.ofNullable(jdbcTemplate
                .queryForObject("select * from category where deleted = false and  name = ?",
                        new CategoryMapper(),
                        name));
    }
}

package uz.md.shopappjdbc.repository.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.OrderProduct;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.OrderProductRepository;
import uz.md.shopappjdbc.repository.contract.OrderRepository;
import uz.md.shopappjdbc.repository.contract.ProductRepository;
import uz.md.shopappjdbc.repository.rowMapper.OrderProductMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderProductRepositoryImpl(JdbcTemplate jdbcTemplate,
                                      @Lazy OrderRepository orderRepository,
                                      ProductRepository productRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderProduct save(OrderProduct entity) {
        Assert.notNull(entity, "Entity must not be null");

        if (entity.getOrder() == null)
            throw new SQLException("ORDER_CANNOT_BE_NULL");

        if (entity.getProduct() == null)
            throw new SQLException("PRODUCT_CANNOT_BE_NULL");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update order_product set deleted = ?, price = ?, quantity = ?, order_id = ?, product_id = ? where id = ?",
                    entity.isDeleted(),
                    entity.getProduct(),
                    entity.getQuantity(),
                    entity.getOrder().getId(),
                    entity.getProduct().getId(),
                    entity.getId()
            );
        } else {
            jdbcTemplate.update(
                    "insert into order_product(deleted, price, quantity, order_id, product_id) " +
                            "values (?,?,?,?,?)",
                    entity.isDeleted(),
                    entity.getPrice(),
                    entity.getQuantity(),
                    entity.getOrder().getId(),
                    entity.getProduct().getId()
            );
        }
        entity = findTheLastSaved();
        return entity;
    }

    private OrderProduct findTheLastSaved() {
        OrderProduct orderProduct = jdbcTemplate
                .queryForObject("select * from order_product where id = (select max(id) from order_product)",
                        new OrderProductMapper());
        if (orderProduct != null) {
            setOrder(orderProduct);
            setProduct(orderProduct);
        }
        return orderProduct;
    }


    @Override
    public List<OrderProduct> saveAll(List<OrderProduct> entities) {
        Assert.notNull(entities, "Entities must not be null");
        List<OrderProduct> result = new ArrayList<>();
        for (OrderProduct entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<OrderProduct> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        OrderProduct orderProduct = jdbcTemplate.queryForObject(
                "select * from orders where deleted = false and id = ?",
                new OrderProductMapper(),
                id);

        if (orderProduct != null) {
//            setOrder(orderProduct);
            setProduct(orderProduct);
            return Optional.of(orderProduct);
        }
        return Optional.empty();
    }

    private void setProduct(OrderProduct orderProduct) {
        orderProduct.setProduct(productRepository
                .findById(orderProduct.getProduct().getId())
                .orElseThrow(() -> new SQLException("PRODUCT_CANNOT_BE_NULL")));
    }

    private void setOrder(OrderProduct orderProduct) {
        orderProduct.setOrder(orderRepository
                .findById(orderProduct.getOrder().getId())
                .orElseThrow(() -> new SQLException("ORDER_CANNOT_BE_NULL")));
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "id cannot be null");
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count()>0 then true else false end from order_product u where deleted = false and u.id = ?",
                Boolean.class,
                id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<OrderProduct> findAll() {

        List<OrderProduct> query = jdbcTemplate.query(
                "select * from order_product  where deleted = false",
                new OrderProductMapper());
//        setOrder(query);
        setProduct(query);
        return query;
    }

    private void setOrder(List<OrderProduct> orderProducts) {
        orderProducts.forEach(this::setOrder);
    }

    private void setProduct(List<OrderProduct> query) {
        for (OrderProduct orderProduct : query) {
            setProduct(orderProduct);
        }

    }

    @Override
    public List<OrderProduct> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "ids must not be null");
        String ids = RepositoryUtil.getAsString(idList);
        List<OrderProduct> query = jdbcTemplate.query(
                "select * from order_product where deleted = false and id in ?",
                new OrderProductMapper(),
                ids);
//        setOrder(query);
        setProduct(query);
        return query;

    }

    @Override
    public long count() {
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
                "select count(id) from orders where deleted = false",
                Long.class));
    }

    @Override
    public void deleteById(Long uuid) {
        Assert.notNull(uuid, "id must not be null");
        jdbcTemplate.update("update orders set deleted = true where id = ?", uuid);
    }

    @Override
    public void delete(OrderProduct entity) {
        Assert.notNull(entity, "entity must not be null");
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> uuids) {
        Assert.notNull(uuids, "ids must not be null");
        String ids = RepositoryUtil.getAsString(uuids);
        jdbcTemplate.update("update orders set deleted = true where id in ?", ids);
    }

    @Override
    public void deleteAll(Iterable<? extends OrderProduct> entities) {
        Assert.notNull(entities, "entities must not be null");
        List<Long> ids = new ArrayList<>();
        entities.forEach(product -> ids.add(product.getId()));
        deleteAllById(ids);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from orders");
    }


    @Override
    public List<OrderProduct> findAllByOrderId(Long id) {
        Assert.notNull(id, "id must not be null");
        List<OrderProduct> query = jdbcTemplate.query(
                "select * from order_product where deleted = false and id = ?",
                new OrderProductMapper(),
                id);
//        setOrder(query);
        setProduct(query);
        return query;
    }
}

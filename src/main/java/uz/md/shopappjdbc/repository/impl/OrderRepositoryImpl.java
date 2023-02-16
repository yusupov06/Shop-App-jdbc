package uz.md.shopappjdbc.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import uz.md.shopappjdbc.domain.Address;
import uz.md.shopappjdbc.domain.Order;
import uz.md.shopappjdbc.domain.OrderProduct;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.domain.enums.OrderStatus;
import uz.md.shopappjdbc.exceptions.SQLException;
import uz.md.shopappjdbc.repository.RepositoryUtil;
import uz.md.shopappjdbc.repository.contract.OrderProductRepository;
import uz.md.shopappjdbc.repository.contract.OrderRepository;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.repository.rowMapper.OrderMapper;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final OrderProductRepository orderProductRepository;

    @Override
    public Order save(Order entity) {
        Assert.notNull(entity, "Entity must not be null");

        if (entity.getAddress() == null || entity.getAddress().getId() == null)
            throw new SQLException("ADDRESS_CANNOT_BE_NULL");
        if (entity.getUser() == null || entity.getUser().getId() == null)
            throw new SQLException("USER_CANNOT_BE_NULL");
        if (entity.getId() != null) {
            jdbcTemplate.update(
                    "update orders set active = ?, added_at = ?, deleted = ?, overall_price = ?, status = ?, updated_by_id = ?, address_id = ?, user_id = ? where id = ?",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getOverallPrice(),
                    entity.getStatus().toString(),
                    UUID.randomUUID(), // TODO get current user and set its id
                    entity.getAddress().getId(),
                    entity.getUser().getId(),
                    entity.getId()
            );

        } else {
            jdbcTemplate.update(
                    "insert into orders(active, added_at, deleted, overall_price, status, updated_by_id, address_id, user_id) " +
                            "values (?,?,?,?,?,?,?,?)",
                    entity.isActive(),
                    LocalDateTime.now(),
                    entity.isDeleted(),
                    entity.getOverallPrice(),
                    entity.getStatus().toString(),
                    UUID.randomUUID(), // TODO get current user and set its id
                    entity.getAddress().getId(),
                    entity.getUser().getId()
            );
        }
        if (entity.getOrderProducts() != null)
            saveOrderProductsIfNotSaved(entity.getOrderProducts());

        entity = getLastSaved();
        return entity;
    }

    private Order getLastSaved() {
        Order order = jdbcTemplate
                .queryForObject("select * from orders where id = (select max(id) from orders)",
                        new OrderMapper());
        if (order != null) {
            setUser(order);
            setAddress(order, order.getUser());
        }
        return order;
    }

    private void saveOrderProductsIfNotSaved(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getId() == null)
                orderProductRepository.save(orderProduct);
        }
    }

    @Override
    public List<Order> saveAll(List<Order> entities) {
        Assert.notNull(entities, "Entities must not be null");
        List<Order> result = new ArrayList<>();
        for (Order entity : entities) {
            result.add(save(entity));
        }
        return result;
    }


    @Override
    public Optional<Order> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        Order order = jdbcTemplate.queryForObject(
                "select * from orders where deleted = false and id = ?",
                new OrderMapper(),
                id);

        if (order != null) {
            setUser(order);
            setAddress(order, order.getUser());
            setOrderProducts(order);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    private void setOrderProducts(Order order) {
        order.setOrderProducts(orderProductRepository
                .findAllByOrderId(order.getId()));
    }

    private void setAddress(Order order, User user) {
        for (Address address : user.getAddresses()) {
            if (address.getId().equals(order.getAddress().getId()))
                order.setAddress(address);
        }
    }

    private void setUser(Order order) {
        order.setUser(userRepository
                .findById(order.getUser().getId())
                .orElseThrow(() -> new SQLException("USER_NOT_FOUND")));
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "Id cannot be null");
        Boolean aBoolean = jdbcTemplate.queryForObject(
                "select case when count(id)>0 then true else false end from orders u where deleted = false and u.id = ?",
                Boolean.class,
                id);
        return aBoolean != null && aBoolean;
    }

    @Override
    public List<Order> findAll() {

        List<Order> query = jdbcTemplate.query(
                "select * from orders  where deleted = false",
                new OrderMapper());
        setUser(query);
        setAddress(query);
        setUser(query);
        setOrderProducts(query);
        return query;
    }

    private void setOrderProducts(List<Order> orders) {
        orders.forEach(this::setOrderProducts);
    }

    private void setAddress(List<Order> orders) {
        orders.forEach(order -> setAddress(order, order.getUser()));
    }

    private void setUser(List<Order> query) {
        for (Order order : query) {
            setUser(order);
        }

    }

    @Override
    public List<Order> findAllById(Iterable<Long> idList) {
        Assert.notNull(idList, "ids must not be null");
        String ids = RepositoryUtil.getAsString(idList);
        List<Order> query = jdbcTemplate.query(
                "select * from orders where deleted = false and id in ?",
                new OrderMapper(),
                ids);

        setUser(query);
        setAddress(query);
        setUser(query);
        setOrderProducts(query);
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
    public void delete(Order entity) {
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
    public void deleteAll(Iterable<? extends Order> entities) {
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
    public Page<Order> findAllByStatus(OrderStatus status, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        List<Order> query = jdbcTemplate.query("select * from orders where deleted = false and status = ? limit ? offset ?",
                new OrderMapper(),
                status.toString(),
                pageSize,
                pageNumber * pageSize);
        // TODO: check pageable offset method

        setUser(query);
        setAddress(query);
        setUser(query);
        setOrderProducts(query);

        return new PageImpl<>(query, pageable, query.size());
    }

    @Override
    public Page<Order> findAllByUserId(UUID userid, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        List<Order> query = jdbcTemplate
                .query("select * from orders where deleted = false and user_id = ? limit ? offset ?",
                        new OrderMapper(),
                        userid,
                        pageSize,
                        pageNumber * pageSize);

        setUser(query);
        setAddress(query);
        setUser(query);
        setOrderProducts(query);

        return new PageImpl<>(query, pageable, query.size());
    }

    @Override
    public Page<Order> findAll(PageRequest pageRequest) {
        List<Order> query = jdbcTemplate.query("select * from orders where deleted = false",
                new OrderMapper());
        return new PageImpl<>(query, pageRequest, query.size());
    }

    @Override
    public Page<Order> execute(String query, Pageable pageable) {
        List<Order> orders = jdbcTemplate.query(query, new OrderMapper());
        return new PageImpl<>(orders, pageable, orders.size());
    }

}

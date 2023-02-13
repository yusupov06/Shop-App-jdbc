package uz.md.shopappjdbc.repository.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;
import uz.md.shopappjdbc.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.md.shopappjdbc.domain.Order;
import uz.md.shopappjdbc.domain.enums.OrderStatus;

import java.net.ContentHandler;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Page<Order> findAllByStatus(OrderStatus status,
                                Pageable pageable);

    Page<Order> findAllByUserId(UUID userid, Pageable pageable);

    Page<Order> findAll(PageRequest pageRequest);
    Page<Order> execute(String query, Pageable pageable);
}

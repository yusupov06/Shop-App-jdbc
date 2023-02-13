package uz.md.shopappjdbc.repository.contract;

import uz.md.shopappjdbc.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.md.shopappjdbc.domain.OrderProduct;

import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {

    List<OrderProduct> findAllByOrderId(Long id);
}

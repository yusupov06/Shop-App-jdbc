package uz.md.shopappjdbc.domain;

import lombok.*;
import uz.md.shopappjdbc.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Order {

    private Long id;
    private User user;
    private LocalDateTime addedAt;
    private OrderStatus status = OrderStatus.PREPARING;
    private boolean active;
    private boolean deleted;
    private Address address;
    private List<OrderProduct> orderProducts;

    private Double overallPrice;

    public Order(Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return getId() != null && getId().equals(((Order) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }


}

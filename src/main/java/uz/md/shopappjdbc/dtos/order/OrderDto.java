package uz.md.shopappjdbc.dtos.order;


import lombok.*;
import uz.md.shopappjdbc.domain.enums.OrderStatus;
import uz.md.shopappjdbc.dtos.address.AddressDto;
import uz.md.shopappjdbc.dtos.orderProduct.OrderProductDto;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDto {
    private Long id;
    private UUID userId;
    private OrderStatus status;
    private Double overallPrice;
    private AddressDto address;
    private List<OrderProductDto> orderProducts;
}

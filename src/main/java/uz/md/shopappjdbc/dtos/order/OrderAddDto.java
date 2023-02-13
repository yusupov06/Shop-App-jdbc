package uz.md.shopappjdbc.dtos.order;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.md.shopappjdbc.dtos.address.AddressAddDto;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderAddDto {

    @NotNull(message = "order user id must not be null")
    private UUID userId;
    private AddressAddDto address;
    private Long addressId;
    private Double overallPrice;

    @NotNull(message = "ordered products must not be null")
    private List<OrderProductAddDto> orderProducts;

}

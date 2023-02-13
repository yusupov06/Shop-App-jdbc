package uz.md.shopappjdbc.dtos.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderProductAddDto {

    @NotNull(message = "ordered product id must not be null ")
    private Long productId;
    @NotNull(message = "order product quantity must not be null ")
    private Integer quantity;
    private Double price;
}

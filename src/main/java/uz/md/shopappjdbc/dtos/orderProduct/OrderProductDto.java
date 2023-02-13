package uz.md.shopappjdbc.dtos.orderProduct;

import lombok.*;
import uz.md.shopappjdbc.dtos.product.ProductDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderProductDto {
    private Long id;
    private Long orderId;
    private ProductDto product;
    private int quantity;
    private double price;
}

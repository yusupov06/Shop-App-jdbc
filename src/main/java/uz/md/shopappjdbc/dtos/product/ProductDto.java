package uz.md.shopappjdbc.dtos.product;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long categoryId;
}

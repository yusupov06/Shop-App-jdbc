package uz.md.shopappjdbc.dtos.product;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductAddDto {

    @NotBlank(message = "Product name must not be empty")
    private String name;
    private String description;

    @NotNull(message = "Product price must not be null")
    private Double price;

    @NotNull(message = "Product category must not be null")
    private Long categoryId;
}

package uz.md.shopappjdbc.dtos.product;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductEditDto extends ProductAddDto {

    @NotNull(message = "product id must not be null")
    private Long id;

    public ProductEditDto(Long id, String name, String description, Double price, Long categoryId) {
        super(name, description, price, categoryId);
        this.id = id;
    }

}

package uz.md.shopappjdbc.dtos.category;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryEditDto extends CategoryAddDTO {

    @NotNull(message = " category id must not be null")
    private Long id;

    public CategoryEditDto(Long id, String name, String description) {
        super(name, description);
        this.id = id;
    }

}

package uz.md.shopappjdbc.domain;

import lombok.*;
import uz.md.shopappjdbc.domain.template.AbsLongEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Product extends AbsLongEntity {

    private String name;
    private String description;
    private Double price;
    private Category category;

    public Product(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return super.getId() != null && super.getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }


}

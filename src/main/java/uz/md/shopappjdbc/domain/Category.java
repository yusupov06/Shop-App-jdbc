package uz.md.shopappjdbc.domain;

import lombok.*;
import uz.md.shopappjdbc.domain.template.AbsLongEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Category extends AbsLongEntity {

    private String name;

    private String description;

    @ToString.Include
    private List<Product> products;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return super.getId() != null && super.getId().equals(((Category) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }


}



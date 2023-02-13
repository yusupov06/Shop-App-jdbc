package uz.md.shopappjdbc.domain;

import lombok.*;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.domain.template.AbsIntegerEntity;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Role extends AbsIntegerEntity {

    private String name;
    private String description;

    private Set<PermissionEnum> permissions;

    public Role(Integer id) {
        super.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return super.getId() != null && super.getId().equals(((Role) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }

}

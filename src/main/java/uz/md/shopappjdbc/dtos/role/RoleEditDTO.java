package uz.md.shopappjdbc.dtos.role;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;

import java.util.Set;

@Getter
public class RoleEditDTO extends RoleAddDTO {

    @NotNull(message = "Role id must not be null")
    private Integer id;

    public RoleEditDTO(Integer id, String name, String description, Set<PermissionEnum> permissions) {
        super(name, description, permissions);
        this.id = id;
    }

}
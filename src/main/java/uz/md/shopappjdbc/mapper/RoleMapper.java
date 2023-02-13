package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.dtos.role.RoleAddDTO;
import uz.md.shopappjdbc.dtos.role.RoleDTO;
import uz.md.shopappjdbc.dtos.role.RoleEditDTO;

@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<Role, RoleDTO> {

    Role fromAddDTO(RoleAddDTO dto);

    Role fromEditDto(@MappingTarget Role role, RoleEditDTO dto);
}

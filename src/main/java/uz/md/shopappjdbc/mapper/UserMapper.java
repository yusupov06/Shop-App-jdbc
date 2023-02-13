package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.dtos.user.UserDto;
import uz.md.shopappjdbc.dtos.user.UserRegisterDto;

@Mapper(componentModel = "spring",
        uses = {AddressMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface UserMapper extends EntityMapper<User, UserDto> {

    @Override
    @Mapping(target = "permissions", source = "role.permissions")
    @Mapping(target = "addresses", expression = " java( addressMapper.toDtoList( entity.getAddresses() ) )")
    @Mapping(target = "isAdmin", expression = " java( isAdmin(entity.getRole()) )")
    UserDto toDto(User entity);

    User fromAddDto(UserRegisterDto dto);

    default boolean isAdmin(Role role) {
        return role.getName().equalsIgnoreCase("ADMIN");
    }

}

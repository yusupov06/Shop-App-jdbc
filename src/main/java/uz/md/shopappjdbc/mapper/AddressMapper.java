package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.md.shopappjdbc.domain.Address;
import uz.md.shopappjdbc.dtos.address.AddressAddDto;
import uz.md.shopappjdbc.dtos.address.AddressDto;
import uz.md.shopappjdbc.dtos.address.AddressEditDto;

@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityMapper<Address, AddressDto> {

    @Override
    @Mapping(target = "userId" , source = "user.id")
    AddressDto toDto(Address entity);

    Address fromAddDto(AddressAddDto dto);

    Address fromEditDto(AddressEditDto editDto);
}

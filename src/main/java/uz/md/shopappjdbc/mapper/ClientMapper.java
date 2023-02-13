package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.md.shopappjdbc.domain.AccessKey;
import uz.md.shopappjdbc.domain.Client;
import uz.md.shopappjdbc.dtos.client.ClientAddDto;
import uz.md.shopappjdbc.dtos.client.ClientDto;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {AccessKey.class, Collectors.class})
public interface ClientMapper extends EntityMapper<Client, ClientDto> {
    Client fromAddDto(ClientAddDto addDto);

    @Override
    @Mapping(target = "accessKeys", expression = " java( entity.getAccessKeys().stream().map(AccessKey::getAccess).collect(Collectors.toList()) ) ")
    ClientDto toDto(Client entity);

    @Override
    @Mapping(target = "accessKeys", ignore = true)
    Client fromDto(ClientDto dto);
}

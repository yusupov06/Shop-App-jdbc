package uz.md.shopappjdbc.dtos.user;

import lombok.*;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.address.AddressDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isAdmin;
    private List<AddressDto> addresses;
    private Set<PermissionEnum> permissions;
}

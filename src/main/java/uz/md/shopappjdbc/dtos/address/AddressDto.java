package uz.md.shopappjdbc.dtos.address;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddressDto {
    private Long id;
    private Integer houseNumber;
    private String street;
    private String city;
    private UUID userId;
}

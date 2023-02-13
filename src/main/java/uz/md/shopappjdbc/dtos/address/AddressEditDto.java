package uz.md.shopappjdbc.dtos.address;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddressEditDto extends AddressAddDto {

    @NotNull(message = "address id must be specified")
    private Long id;
}

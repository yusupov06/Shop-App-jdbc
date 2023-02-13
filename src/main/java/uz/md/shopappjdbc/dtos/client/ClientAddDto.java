package uz.md.shopappjdbc.dtos.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientAddDto {
    private String username;
    private String phoneNumber;
}

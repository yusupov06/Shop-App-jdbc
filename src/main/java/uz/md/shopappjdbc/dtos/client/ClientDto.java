package uz.md.shopappjdbc.dtos.client;

import lombok.*;
import uz.md.shopappjdbc.domain.AccessKey;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ClientDto {
    private Long id;
    private String username;
    private String phoneNumber;
    private List<String> accessKeys;
}

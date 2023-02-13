package uz.md.shopappjdbc.dtos.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserRegisterDto {

    private String firstName;

    private String lastName;

    @NotBlank(message = "PhoneNumber must not be empty")
    private String phoneNumber;

    @NotBlank(message = "Password must not be empty")
    private String password;

}

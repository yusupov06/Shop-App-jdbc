package uz.md.shopappjdbc.dtos.user;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserLoginDto {

    @NotBlank(message = "PhoneNumber can't be empty")
    private String phoneNumber;

    @NotBlank(message = "Password can't be empty")
    private String password;

}

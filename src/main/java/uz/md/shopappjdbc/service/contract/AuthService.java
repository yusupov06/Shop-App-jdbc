package uz.md.shopappjdbc.service.contract;

import org.springframework.security.core.userdetails.UserDetailsService;
import uz.md.shopappjdbc.dtos.*;
import uz.md.shopappjdbc.dtos.user.UserLoginDto;
import uz.md.shopappjdbc.dtos.user.UserRegisterDto;

public interface AuthService extends UserDetailsService {

    ApiResult<Void> register(UserRegisterDto dto);

    ApiResult<TokenDTO> login(UserLoginDto dto);

    ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken);

    ApiResult<Void> activate(String activationCode);

}

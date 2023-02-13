package uz.md.shopappjdbc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.TokenDTO;
import uz.md.shopappjdbc.dtos.user.UserLoginDto;
import uz.md.shopappjdbc.dtos.user.UserRegisterDto;
import uz.md.shopappjdbc.service.contract.AuthService;
import uz.md.shopappjdbc.utils.AppConstants;

@RestController
@RequestMapping(AuthController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints for Auth")
@Slf4j
public class AuthController {

    /**
     * AuthController URL endpoints
     */
    public static final String BASE_URL = AppConstants.BASE_URL + "auth";
    public static final String REGISTER_URL = "/sign-up";
    public static final String LOGIN_URL = "/sign-in";

    private final AuthService authService;

    /**
     * @param dto for registering user
     * @return ApiResult that's success is true if successfully registered or else false
     */
    @PostMapping(value = REGISTER_URL)
    ApiResult<Void> register(@RequestBody @Valid UserRegisterDto dto) {
        log.info("Request body: {}", dto.toString());
        return authService.register(dto);
    }

    /**
     * @param dto for login user
     * @return {@link TokenDTO}
     */
    @PostMapping(value = LOGIN_URL)
    ApiResult<TokenDTO> login(@RequestBody @Valid UserLoginDto dto) {
        log.info("Request body: {}", dto);
        return authService.login(dto);
    }

    @Operation(description = "Refresh token")
    @GetMapping(value = "/refresh")
    ApiResult<TokenDTO> refreshToken(@RequestHeader(value = "Authorization") String accessToken, @RequestHeader(value = "RefreshToken") String refreshToken) {
        log.info("refresh token method : {}, {}", accessToken,refreshToken);
        ApiResult<TokenDTO> result = authService.refreshToken(accessToken, refreshToken);
        log.info("refresh token method : {}", result);
        return result;
    }

}

package uz.md.shopappjdbc.rest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uz.md.shopappjdbc.IntegrationTest;
import uz.md.shopappjdbc.controller.AuthController;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.TokenDTO;
import uz.md.shopappjdbc.dtos.user.UserLoginDto;
import uz.md.shopappjdbc.dtos.user.UserRegisterDto;
import uz.md.shopappjdbc.service.contract.AuthService;
import uz.md.shopappjdbc.util.TestUtil;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uz.md.shopappjdbc.controller.AuthController.*;
/**
 * Integration tests for {@link AuthController}
 */
@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    @Test
    void shouldRegister() throws Exception {

        UserRegisterDto registerDto = new UserRegisterDto(
                "user1",
                "user1",
                "+998931112233",
                "123");

        ApiResult<Void> result = ApiResult.successResponse();
        when(authService.register(ArgumentMatchers.any())).thenReturn(result);
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL+REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(registerDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldLogin() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto("yusupov@gmail.com", "123");
        TokenDTO tokenDTO = new TokenDTO();

        when(authService.login(userLoginDto)).thenReturn(ApiResult.successResponse(tokenDTO));
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL+LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userLoginDto)))
                .andExpect(status().isOk());
    }




}

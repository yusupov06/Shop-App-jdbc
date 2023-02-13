package uz.md.shopappjdbc.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uz.md.shopappjdbc.IntegrationTest;
import uz.md.shopappjdbc.controller.AuthController;
import uz.md.shopappjdbc.controller.UserController;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.user.UserDto;
import uz.md.shopappjdbc.dtos.user.UserLoginDto;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.service.contract.UserService;
import uz.md.shopappjdbc.util.TestUtil;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.md.shopappjdbc.controller.AuthController.LOGIN_URL;
import static uz.md.shopappjdbc.controller.UserController.BASE_URL;

/**
 * Integration tests for {@link UserController}
 */
@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;


    @Value("${app.admin.firstName}")
    private String firstName;

    @Value("${app.admin.phoneNumber}")
    private String phoneNumber;

    @Value("${app.admin.password}")
    private String password;

    @MockBean
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private static boolean setUpIsDone = false;

    private String accessToken;


    @BeforeEach
    void init() throws Exception {
        if (!setUpIsDone) {
            addAdmin();
            saveUserRole();
            setUpIsDone = true;
        }
        accessToken = obtainAccessToken();
    }

    private void saveUserRole() {
        roleRepository.save(
                new Role("USER",
                        "System USER",
                        Set.of(PermissionEnum.GET_PRODUCT)
                )
        );
    }

    private void addAdmin() {
        userRepository.save(new User(
                firstName,
                "",
                phoneNumber,
                passwordEncoder.encode(password),
                addAdminRole(),
                true
        ));
    }

    private Role addAdminRole() {
        return roleRepository.save(
                new Role("ADMIN",
                        "System owner",
                        Set.of(PermissionEnum.values())
                )
        );
    }

    @SuppressWarnings("unchecked")
    private String obtainAccessToken() throws Exception {

        UserLoginDto userLoginDto = new UserLoginDto(phoneNumber, password);

        ResultActions result
                = mvc.perform(MockMvcRequestBuilders
                        .post(AuthController.BASE_URL + LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userLoginDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        Map<String, String> data = (LinkedHashMap<String, String>) jsonParser.parseMap(resultString).get("data");
        System.out.println("data = " + data);
        return data.get("accessToken");
    }

    @Test
    void shouldGetById() throws Exception {
        UserDto userDto = new UserDto(UUID.randomUUID(),
                "firstname",
                "lastname",
                "+998931668648",
                false,
                new ArrayList<>(),
                Set.of(PermissionEnum.GET_PRODUCT));

        ApiResult<UserDto> result = ApiResult.successResponse(userDto);
        when(userService.findById(userDto.getId())).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/"+userDto.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(userDto.getId().toString()))
                .andExpect(jsonPath("$.data.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(userDto.getPhoneNumber()))
                .andExpect(jsonPath("$.data.admin").value(userDto.isAdmin()))
        ;
    }

    @Test
    void shouldDelete() throws Exception {
        UUID uuid = UUID.randomUUID();
        ApiResult<Void> result = ApiResult.successResponse();
        when(userService.delete(uuid)).thenReturn(result);
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/delete/"+uuid)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }


}

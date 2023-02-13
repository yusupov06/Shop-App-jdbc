package uz.md.shopappjdbc.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import uz.md.shopappjdbc.controller.CategoryController;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryAddDTO;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryEditDto;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.dtos.user.UserLoginDto;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.service.contract.CategoryService;
import uz.md.shopappjdbc.util.TestUtil;
import uz.md.shopappjdbc.domain.Role;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.md.shopappjdbc.controller.AuthController.LOGIN_URL;
import static uz.md.shopappjdbc.controller.CategoryController.BASE_URL;

/**
 * Integration tests for {@link CategoryController}
 */
@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {


    @Value("${app.admin.firstName}")
    private String firstName;

    @Value("${app.admin.phoneNumber}")
    private String phoneNumber;

    @Value("${app.admin.password}")
    private String password;

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CategoryService categoryService;
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
    private String obtainAccessToken(String phoneNumber, String password) throws Exception {

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
    void shouldAdd() throws Exception {

        accessToken = obtainAccessToken(phoneNumber, password);
        CategoryAddDTO addDto = new CategoryAddDTO(
                "category",
                "description");
        CategoryDto categoryDto = new CategoryDto(1L, addDto.getName(), addDto.getDescription());
        ApiResult<CategoryDto> result = ApiResult.successResponse(categoryDto);
        when(categoryService.add(ArgumentMatchers.any())).thenReturn(result);

        mvc.perform(post(BASE_URL + "/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(addDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("category"))
                .andExpect(jsonPath("$.data.description").value("description"))
        ;
    }

    @Test
    void shouldNotAddWithOutPermission() throws Exception {

        Role role = roleRepository.save(new Role("USER", "description", Set.of(PermissionEnum.GET_CATEGORY)));
        userRepository.save(new User("ali", "ali", "+998931001122", passwordEncoder.encode("123"), role, true));

        accessToken = obtainAccessToken("+998931001122", "123");
        CategoryAddDTO addDto = new CategoryAddDTO(
                "category",
                "description");

        mvc.perform(post(BASE_URL + "/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(addDto)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    void shouldGetAll() throws Exception {
        accessToken = obtainAccessToken(phoneNumber, password);
        List<CategoryDto> categoryDtos = List.of(
                new CategoryDto(1L, "category1", "description"),
                new CategoryDto(2L, "category2", "description")
        );

        ApiResult<List<CategoryDto>> result = ApiResult.successResponse(categoryDtos);
        when(categoryService.getAll()).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(categoryDtos.get(0).getId().intValue()))
                .andExpect(jsonPath("$.data[0].name").value(categoryDtos.get(0).getName()))
                .andExpect(jsonPath("$.data[0].description").value(categoryDtos.get(0).getDescription()))
                .andExpect(jsonPath("$.data[1].id").value(categoryDtos.get(1).getId().intValue()))
                .andExpect(jsonPath("$.data[1].name").value(categoryDtos.get(1).getName()))
                .andExpect(jsonPath("$.data[1].description").value(categoryDtos.get(1).getDescription()))
        ;
    }

    @Test
    void shouldGetAllForInfo() throws Exception {
        accessToken = obtainAccessToken(phoneNumber, password);
        List<CategoryInfoDto> categoryDtos = List.of(
                new CategoryInfoDto(1L, "category1", "description"),
                new CategoryInfoDto(2L, "category2", "description")
        );

        ApiResult<List<CategoryInfoDto>> result = ApiResult.successResponse(categoryDtos);
        when(categoryService.getAllForInfo()).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/all")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(categoryDtos.get(0).getId().intValue()))
                .andExpect(jsonPath("$.data[0].name").value(categoryDtos.get(0).getName()))
                .andExpect(jsonPath("$.data[0].description").value(categoryDtos.get(0).getDescription()))
                .andExpect(jsonPath("$.data[1].id").value(categoryDtos.get(1).getId().intValue()))
                .andExpect(jsonPath("$.data[1].name").value(categoryDtos.get(1).getName()))
                .andExpect(jsonPath("$.data[1].description").value(categoryDtos.get(1).getDescription()))
        ;
    }

    @Test
    void shouldGetById() throws Exception {
        accessToken = obtainAccessToken(phoneNumber, password);
        CategoryDto categoryDto = new CategoryDto(1L, "category2", "description");

        ApiResult<CategoryDto> result = ApiResult.successResponse(categoryDto);
        when(categoryService.findById(1L)).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(categoryDto.getId().intValue()))
                .andExpect(jsonPath("$.data.name").value(categoryDto.getName()))
                .andExpect(jsonPath("$.data.description").value(categoryDto.getDescription()))
        ;
    }

    @Test
    void shouldEdit() throws Exception {

        accessToken = obtainAccessToken(phoneNumber, password);
        CategoryEditDto addDto = new CategoryEditDto(
                1L,
                "category",
                "description");

        CategoryDto categoryDto = new CategoryDto(1L, addDto.getName(), addDto.getDescription());

        ApiResult<CategoryDto> result = ApiResult.successResponse(categoryDto);


        when(categoryService.edit(ArgumentMatchers.any())).thenReturn(result);
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/edit")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(addDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("category"))
                .andExpect(jsonPath("$.data.description").value("description"))
        ;
    }

    @Test
    void shouldDelete() throws Exception {

        accessToken = obtainAccessToken(phoneNumber, password);
        ApiResult<Void> result = ApiResult.successResponse();
        when(categoryService.delete(ArgumentMatchers.any())).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/delete/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }


}

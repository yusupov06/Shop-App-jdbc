package uz.md.shopappjdbcjdbc.rest;

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
import uz.md.shopappjdbc.controller.ProductController;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.product.ProductAddDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.product.ProductEditDto;
import uz.md.shopappjdbc.dtos.user.UserLoginDto;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.service.contract.ProductService;
import uz.md.shopappjdbc.util.TestUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.md.shopappjdbc.controller.AuthController.LOGIN_URL;
import static uz.md.shopappjdbc.controller.ProductController.BASE_URL;

/**
 * Integration tests for {@link ProductController}
 */
@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {

    @Value("${app.admin.firstName}")
    private String firstName;

    @Value("${app.admin.phoneNumber}")
    private String phoneNumber;

    @Value("${app.admin.password}")
    private String password;

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ProductService productService;

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
    void shouldAdd() throws Exception {

        ProductAddDto addDto = new ProductAddDto(
                "product",
                "description",
                500.0,
                1L);
        ProductDto productDto = new ProductDto(1L, addDto.getName(), addDto.getDescription(), 500.0, 1L);

        ApiResult<ProductDto> result = ApiResult.successResponse(productDto);

        when(productService.add(ArgumentMatchers.any())).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(addDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("product"))
                .andExpect(jsonPath("$.data.description").value("description"))
                .andExpect(jsonPath("$.data.price").value(500.0))
                .andExpect(jsonPath("$.data.categoryId").value(1L))
        ;
    }

    @Test
    void shouldGetAllByCategory() throws Exception {

        List<ProductDto> productDtos = List.of(
                new ProductDto(1L, "product1", "description", 500.0, 1L),
                new ProductDto(2L, "product2", "description", 500.0, 1L)
        );

        ApiResult<List<ProductDto>> result = ApiResult.successResponse(productDtos);
        when(productService.getAllByCategory(ArgumentMatchers.any())).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/category/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(productDtos.get(0).getId().intValue()))
                .andExpect(jsonPath("$.data[0].name").value(productDtos.get(0).getName()))
                .andExpect(jsonPath("$.data[0].description").value(productDtos.get(0).getDescription()))
                .andExpect(jsonPath("$.data[1].id").value(productDtos.get(1).getId().intValue()))
                .andExpect(jsonPath("$.data[1].name").value(productDtos.get(1).getName()))
                .andExpect(jsonPath("$.data[1].description").value(productDtos.get(1).getDescription()))
        ;
    }

    @Test
    void shouldGetById() throws Exception {
        ProductDto productDto = new ProductDto(1L, "product2", "description", 500.0, 1L);

        ApiResult<ProductDto> result = ApiResult.successResponse(productDto);
        when(productService.findById(1L)).thenReturn(result);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(productDto.getId().intValue()))
                .andExpect(jsonPath("$.data.name").value(productDto.getName()))
                .andExpect(jsonPath("$.data.description").value(productDto.getDescription()))
                .andExpect(jsonPath("$.data.price").value(productDto.getPrice()))
                .andExpect(jsonPath("$.data.categoryId").value(productDto.getCategoryId()))
        ;
    }

    @Test
    void shouldEdit() throws Exception {

        ProductEditDto addDto = new ProductEditDto(
                1L,
                "product",
                "description",
                500.0,
                1L);

        ProductDto productDto = new ProductDto(1L, addDto.getName(), addDto.getDescription(),addDto.getPrice(),addDto.getCategoryId());
        ApiResult<ProductDto> result = ApiResult.successResponse(productDto);
        when(productService.edit(ArgumentMatchers.any())).thenReturn(result);
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/edit")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(addDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("product"))
                .andExpect(jsonPath("$.data.description").value("description"))
        ;
    }

    @Test
    void shouldDelete() throws Exception {

        ApiResult<Void> result = ApiResult.successResponse();
        when(productService.delete(ArgumentMatchers.any())).thenReturn(result);
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/delete/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }


}

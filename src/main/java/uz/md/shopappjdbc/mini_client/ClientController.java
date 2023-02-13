package uz.md.shopappjdbc.mini_client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.domain.Client;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.dtos.client.ClientAddDto;
import uz.md.shopappjdbc.dtos.client.ClientDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.service.contract.ClientService;
import uz.md.shopappjdbc.utils.AppConstants;

import java.util.List;

@RestController
@RequestMapping(ClientController.BASE_URL)
@RequiredArgsConstructor
public class ClientController {

    public static final String BASE_URL = AppConstants.BASE_URL + "client";
    private static final String GET_ALL_CATEGORIES = "/get_all_categories";
    private static final String GET_PRODUCTS_BY_CATEGORY = "/get_products";

    private final ClientService clientService;

    @PostMapping
    public ApiResult<ClientDto> getKey(@RequestBody @Valid ClientAddDto addDto){
        return clientService.getKey(addDto);
    }

    @GetMapping(GET_ALL_CATEGORIES)
    public ApiResult<List<CategoryInfoDto>> getAllCategories(@RequestParam String access_key) {
        return clientService.getAllCategories(access_key);
    }

    @GetMapping(GET_PRODUCTS_BY_CATEGORY)
    public ApiResult<List<ProductDto>> getAllProductsByCategory(@RequestParam String access_key, @RequestParam String category_name) {
        return clientService.getAllProductsByCategory(access_key, category_name);
    }

}

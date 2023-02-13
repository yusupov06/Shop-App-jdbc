package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.dtos.client.ClientAddDto;
import uz.md.shopappjdbc.dtos.client.ClientDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;

import java.util.List;

public interface ClientService {
    ApiResult<List<CategoryInfoDto>> getAllCategories(String accessKey);

    ApiResult<List<ProductDto>> getAllProductsByCategory(String accessKey, String categoryName);

    ApiResult<ClientDto> getKey(ClientAddDto addDto);
}

package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.product.ProductAddDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.product.ProductEditDto;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;

import java.util.List;

public interface ProductService {

    ApiResult<ProductDto> findById(Long id);

    ApiResult<ProductDto> add(ProductAddDto dto);

    ApiResult<ProductDto> edit(ProductEditDto editDto);

    ApiResult<Void> delete(Long id);

    ApiResult<List<ProductDto>> getAllByCategory(Long id);

    ApiResult<List<ProductDto>> findAllBySimpleSearch(SimpleSearchRequest request);

    ApiResult<List<ProductDto>> findAllBySort(SimpleSortRequest request);

    ApiResult<List<ProductDto>> findAllByPagination(String page);

    ApiResult<List<ProductDto>> findAllByFilter(FilterRequest request);
}

package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryAddDTO;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryEditDto;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;

import java.util.List;

public interface CategoryService {

    ApiResult<CategoryDto> add(CategoryAddDTO dto);

    ApiResult<CategoryDto> findById(Long id);

    ApiResult<CategoryDto> edit(CategoryEditDto editDto);

    ApiResult<Void> delete(Long id);

    ApiResult<List<CategoryDto>> getAll();

    ApiResult<List<CategoryInfoDto>> getAllForInfo();
}

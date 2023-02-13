package uz.md.shopappjdbc.service.impl;

import org.springframework.stereotype.Service;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryAddDTO;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryEditDto;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.exceptions.AlreadyExistsException;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.mapper.CategoryMapper;
import uz.md.shopappjdbc.repository.contract.CategoryRepository;
import uz.md.shopappjdbc.service.contract.CategoryService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ApiResult<CategoryDto> add(CategoryAddDTO dto) {

        if (categoryRepository.existsByName(dto.getName()))
            throw new AlreadyExistsException("CATEGORY_NAME_ALREADY_EXISTS");

        return ApiResult
                .successResponse(categoryMapper
                        .toDto(categoryRepository
                                .save(categoryMapper
                                        .fromAddDto(dto))));
    }

    @Override
    public ApiResult<CategoryDto> findById(Long id) {
        return ApiResult.successResponse(categoryMapper
                .toDto(categoryRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            throw new NotFoundException("CATEGORY_NOT_FOUND");
                        })));
    }

    @Override
    public ApiResult<CategoryDto> edit(CategoryEditDto editDto) {

        Category editing = categoryRepository
                .findById(editDto.getId())
                .orElseThrow(() -> {
                    throw new NotFoundException("CATEGORY_NOT_FOUND");
                });

        if (categoryRepository.existsByNameAndIdIsNot(editDto.getName(), editing.getId()))
            throw new AlreadyExistsException("CATEGORY_NAME_ALREADY_EXISTS");

        Category category = categoryMapper.fromEditDto(editDto, editing);

        return ApiResult.successResponse(categoryMapper
                .toDto(categoryRepository.save(category)));
    }

    @Override
    public ApiResult<List<CategoryDto>> getAll() {
        return ApiResult.successResponse(categoryMapper
                .toDtoList(new ArrayList<>(categoryRepository.findAll())));
    }

    @Override
    public ApiResult<List<CategoryInfoDto>> getAllForInfo() {
        return ApiResult.successResponse(categoryRepository.findAllForInfo());
    }

    @Override
    public ApiResult<Void> delete(Long id) {

        if (!categoryRepository.existsById(id))
            throw new NotFoundException("CATEGORY_NOT_FOUND");

        categoryRepository.deleteById(id);
        return ApiResult.successResponse();
    }
}

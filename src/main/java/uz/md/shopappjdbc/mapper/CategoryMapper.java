package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.dtos.category.CategoryAddDTO;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryEditDto;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CategoryMapper extends EntityMapper<Category, CategoryDto> {

    Category fromAddDto(CategoryAddDTO dto);

    Category fromEditDto(CategoryEditDto dto, @MappingTarget Category category);

    @Override
    @Mapping(target = "products", expression = " java( productMapper.toDtoList( entity.getProducts() ) ) ")
    CategoryDto toDto(Category entity);
}
package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.dtos.product.ProductAddDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.product.ProductEditDto;

@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<Product, ProductDto> {

    Product fromAddDto(ProductAddDto dto);

    @Override
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product entity);

    Product fromEditDto(ProductEditDto editDto, @MappingTarget Product product);
}

package uz.md.shopappjdbc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.product.ProductAddDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.product.ProductEditDto;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;
import uz.md.shopappjdbc.exceptions.AlreadyExistsException;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.mapper.ProductMapper;
import uz.md.shopappjdbc.repository.contract.CategoryRepository;
import uz.md.shopappjdbc.repository.contract.ProductRepository;
import uz.md.shopappjdbc.service.contract.ProductService;
import uz.md.shopappjdbc.service.query.QueryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final QueryService queryService;
    private Product getById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("PRODUCT_NOT_FOUND_WITH_ID");
                });
    }

    @Override
    public ApiResult<ProductDto> findById(Long id) {
        Product byId = getById(id);
        return ApiResult.successResponse(
                productMapper.toDto(byId));
    }

    @Override
    public ApiResult<ProductDto> add(ProductAddDto dto) {

        if (productRepository.existsByName(dto.getName()))
            throw new AlreadyExistsException("PRODUCT_NAME_ALREADY_EXISTS");

        Product product = productMapper.fromAddDto(dto);

        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND"));

        product.setCategory(category);
        return ApiResult
                .successResponse(productMapper
                        .toDto(productRepository
                                .save(product)));
    }

    @Override
    public ApiResult<ProductDto> edit(ProductEditDto editDto) {

        Product product = productRepository
                .findById(editDto.getId())
                .orElseThrow(() -> {
                    throw new NotFoundException("PRODUCT_NOT_FOUND");
                });

        if (productRepository.existsByNameAndIdIsNot(editDto.getName(), product.getId()))
            throw new AlreadyExistsException("PRODUCT_NAME_ALREADY_EXISTS");

        Product edited = productMapper.fromEditDto(editDto, product);

        edited.setCategory(categoryRepository
                .findById(editDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND")));

        return ApiResult
                .successResponse(productMapper
                        .toDto(productRepository.save(edited)));
    }

    @Override
    public ApiResult<Void> delete(Long id) {
        if (!productRepository.existsById(id))
            throw new NotFoundException("PRODUCT_DOES_NOT_EXIST");
        productRepository.deleteById(id);
        return ApiResult.successResponse();
    }

    @Override
    public ApiResult<List<ProductDto>> getAllByCategory(Long id) {
        if (!categoryRepository.existsById(id))
            throw new NotFoundException("CATEGORY_NOT_FOUND_WITH_ID");
        return ApiResult.successResponse(
                productMapper
                        .toDtoList(productRepository
                                .findAllByCategory_Id(id)));
    }

    @Override
    public ApiResult<List<ProductDto>> findAllBySimpleSearch(SimpleSearchRequest request) {

        String query = queryService.generateSimpleSearchQuery("product", request);

        return ApiResult
                .successResponse(productMapper
                        .toDtoList(productRepository
                                .execute(query, PageRequest.of(request.getPage(), request.getPageCount()))
                                .getContent()));
    }

    @Override
    public ApiResult<List<ProductDto>> findAllBySort(SimpleSortRequest request) {
        String query = queryService.generateSimpleSortQuery("product", request);
        return ApiResult
                .successResponse(productMapper
                        .toDtoList(productRepository
                                .execute(query, PageRequest.of(request.getPage(), request.getPageCount()))
                                .getContent()));
    }

    @Override
    public ApiResult<List<ProductDto>> findAllByPagination(String page) {
        String[] split = page.split("-");
        int p = Integer.parseInt(split[0]);
        int c = Integer.parseInt(split[1]);
        return ApiResult.successResponse(productMapper
                .toDtoList(productRepository
                        .findAll(PageRequest.of(p, c))
                        .getContent()));
    }

    @Override
    public ApiResult<List<ProductDto>> findAllByFilter(FilterRequest request) {
        String query = queryService.generateFilterQuery("product", request);
        return ApiResult
                .successResponse(productMapper
                        .toDtoList(productRepository
                                .execute(query, PageRequest.of(request.getPage(), request.getPageCount()))
                                .getContent()));
    }
}

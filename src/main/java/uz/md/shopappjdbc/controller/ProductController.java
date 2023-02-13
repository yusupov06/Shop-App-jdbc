package uz.md.shopappjdbc.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.aop.annotation.CheckAuth;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.product.ProductAddDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.product.ProductEditDto;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;
import uz.md.shopappjdbc.service.contract.ProductService;
import uz.md.shopappjdbc.utils.AppConstants;

import java.util.List;

@RestController
@RequestMapping(ProductController.BASE_URL + "/")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    public static final String BASE_URL = AppConstants.BASE_URL + "product";

    private final ProductService productService;

    /**
     * Get list of products by category
     *
     * @param id category's id
     * @return list of products
     */
    @GetMapping("/category/{id}")
    @CheckAuth(permission = PermissionEnum.GET_PRODUCT)
    public ApiResult<List<ProductDto>> getAllByCategory(@PathVariable Long id) {
        log.info("getAllByCategory called with category id {}", id);
        return productService.getAllByCategory(id);
    }

    /**
     * Get a product by id
     *
     * @param id product's id
     * @return found product
     */
    @GetMapping("/{id}")
    @CheckAuth(permission = PermissionEnum.GET_PRODUCT)
    public ApiResult<ProductDto> getById(@PathVariable Long id) {
        log.info("getById called with id {}", id);
        return productService.findById(id);
    }

    /**
     * Adds a product
     *
     * @param dto product add dto
     * @return added product
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(description = "Add a product")
    @CheckAuth(permission = PermissionEnum.ADD_PRODUCT)
    public ApiResult<ProductDto> add(@RequestBody @Valid ProductAddDto dto) {
        log.info("Product Add");
        log.info("Request body {}", dto);
        return productService.add(dto);
    }

    /**
     * edits the product
     *
     * @param editDto product edit dto
     * @return edited product
     */
    @PutMapping("/edit")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(description = "edit product")
    @CheckAuth(permission = PermissionEnum.EDIT_PRODUCT)
    public ApiResult<ProductDto> edit(@RequestBody @Valid ProductEditDto editDto) {
        log.info("edit product");
        log.info("Request body {}", editDto);
        return productService.edit(editDto);
    }

    /**
     * deletes the product
     *
     * @param id product id
     * @return deleted product
     */
    @DeleteMapping("/delete/{id}")
    @ApiResponse(description = "Delete a product")
    @CheckAuth(permission = PermissionEnum.DELETE_PRODUCT)
    public ApiResult<Void> delete(@PathVariable Long id) {
        log.info("delete product with id {}", id);
        return productService.delete(id);
    }

    /**
     * Simple Search for products
     *
     * @param request simple search request
     * @return List of products
     */
    @PostMapping("/search")
    @ApiResponse(description = "Searching products")
    @CheckAuth(permission = PermissionEnum.GET_PRODUCT)
    public ApiResult<List<ProductDto>> getProductsBySimpleSearch(@RequestBody @Valid SimpleSearchRequest request) {
        log.info("get products by simple search request");
        log.info("Request body {}", request);
        return productService.findAllBySimpleSearch(request);
    }

    /**
     * Simple Sorting for products
     *
     * @param request sorting products dto
     * @return List of products sorted
     */
    @PostMapping("/sorting")
    @ApiResponse(description = "List of products sorted")
    @CheckAuth(permission = PermissionEnum.GET_PRODUCT)
    public ApiResult<List<ProductDto>> getProductsBySort(@RequestBody @Valid SimpleSortRequest request) {
        log.info("getProductsBySort");
        log.info("Request body is: {}", request);
        return productService.findAllBySort(request);
    }

    /**
     * Simple Sorting for products
     *
     * @param page - pagination
     * @return List of products sorted
     */
    @GetMapping("/page/{page}")
    @ApiResponse(description = "List of products sorted")
    @CheckAuth(permission = PermissionEnum.GET_PRODUCT)
    public ApiResult<List<ProductDto>> getProductsByPagination(@PathVariable String page) {
        log.info("getProductsBySort");
        log.info("Request body is: {}", page);
        return productService.findAllByPagination(page);
    }

    @PostMapping("/filter")
    @ApiResponse(description = "List of products filtered")
    @CheckAuth(permission = PermissionEnum.GET_PRODUCT)
    public ApiResult<List<ProductDto>> getProductsByFilter(@RequestBody @Valid FilterRequest request) {
        log.info("getProductsBySort");
        log.info("Request body is: {}", request);
        return productService.findAllByFilter(request);
    }



}

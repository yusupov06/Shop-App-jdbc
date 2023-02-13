package uz.md.shopappjdbc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.aop.annotation.CheckAuth;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryAddDTO;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryEditDto;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.service.contract.CategoryService;
import uz.md.shopappjdbc.utils.AppConstants;

import java.util.List;

@RestController
@RequestMapping(CategoryController.BASE_URL + "/")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    public static final String BASE_URL = AppConstants.BASE_URL + "category";

    private final CategoryService categoryService;

    /**
     * Gets all categories
     *
     * @return List of all categories
     */
    @GetMapping
    @CheckAuth(permission = PermissionEnum.GET_CATEGORY)
    public ApiResult<List<CategoryDto>> getAll() {
        log.info("Getting all categories");
        return categoryService.getAll();
    }

    /**
     * Gets all categories
     *
     * @return List of all categories
     */
    @GetMapping("/all")
    @CheckAuth(permission = PermissionEnum.GET_CATEGORY)
    public ApiResult<List<CategoryInfoDto>> getAllForInfo() {
        log.info("getting all categories");
        return categoryService.getAllForInfo();
    }

    /**
     * Get a category by its id
     *
     * @param id category id
     * @return found category
     */
    @GetMapping("/{id}")
    @CheckAuth(permission = PermissionEnum.GET_CATEGORY)
    public ApiResult<CategoryDto> getById(@PathVariable Long id) {
        log.info("Getting category by id: {}", id);
        return categoryService.findById(id);
    }

    /**
     * Add a new category
     *
     * @param dto for adding the category
     * @return newly added category
     */
    @PostMapping("/add")
    @CheckAuth(permission = PermissionEnum.ADD_CATEGORY)
    public ApiResult<CategoryDto> add(@RequestBody @Valid CategoryAddDTO dto) {
        log.info("adding category");
        log.info("Request body: {} ", dto);
        return categoryService.add(dto);
    }

    /**
     * Edits a category
     *
     * @param editDto for editing the category
     * @return edited category
     */
    @PutMapping("/edit")
    @CheckAuth(permission = PermissionEnum.EDIT_CATEGORY)
    public ApiResult<CategoryDto> edit(@RequestBody @Valid CategoryEditDto editDto) {
        log.info("editing category");
        log.info("Request body : {} ", editDto);
        return categoryService.edit(editDto);
    }

    /**
     * Deletes a category
     *
     * @param id deleting category's id
     * @return success if category was successfully deleted or else failure
     */
    @DeleteMapping("/delete/{id}")
    @CheckAuth(permission = PermissionEnum.DELETE_CATEGORY)
    public ApiResult<Void> delete(@PathVariable Long id) {
        log.info("Deleting category by id {}", id);
        return categoryService.delete(id);
    }


}

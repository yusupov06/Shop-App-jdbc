package uz.md.shopappjdbc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import uz.md.shopappjdbc.IntegrationTest;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.category.CategoryAddDTO;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryEditDto;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.exceptions.AlreadyExistsException;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.repository.contract.CategoryRepository;
import uz.md.shopappjdbc.repository.contract.ProductRepository;
import uz.md.shopappjdbc.service.contract.CategoryService;
import uz.md.shopappjdbc.util.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@Transactional
@ActiveProfiles("test")
public class CategoryServiceTest {

    private static final String DEFAULT_NAME = "Laptop";

    private static final String DEFAULT_DESCRIPTION = " laptops ";

    private static final String ANOTHER_CATEGORY_NAME = "Mobile Devices";
    private static final String ANOTHER_CATEGORY_DESCRIPTION = " mobile devices ";

    @Autowired
    @Qualifier("categoryRepositoryImpl")
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    private Category category;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void init() {
        category = new Category();
        category.setName(DEFAULT_NAME);
        category.setDescription(DEFAULT_DESCRIPTION);
        category.setDeleted(false);
        category.setActive(true);
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAddCategory() {

        CategoryAddDTO addDto = new CategoryAddDTO("category", "description");
        ApiResult<CategoryDto> result = categoryService.add(addDto);

        assertTrue(result.isSuccess());
        List<Category> all = categoryRepository.findAll();
        Category category1 = all.get(0);

        assertEquals(category1.getName(), addDto.getName());
        assertEquals(category1.getDescription(), addDto.getDescription());
    }

    @Test
    @Transactional
    void shouldNotAddWithAlreadyExistedName() {
        categoryRepository.save(category);
        CategoryAddDTO addDto = new CategoryAddDTO(category.getName(), "user1");
        assertThrows(AlreadyExistsException.class, () -> categoryService.add(addDto));
    }

    @Test
    @Transactional
    void shouldFindById() {

        productRepository.deleteAll();

        category.setProducts(new ArrayList<>(List.of(
                new Product("Hp Laptop", " desc ", 600.0, category),
                new Product("Acer nitro", " desc ", 600.0, category),
                new Product("Asus vivobook", " desc ", 600.0, category)
        )));

        categoryRepository.save(category);
        ApiResult<CategoryDto> result = categoryService.findById(category.getId());
        assertTrue(result.isSuccess());
        CategoryDto data = result.getData();
        assertNotNull(data.getId());
        assertEquals(data.getId(), category.getId());
        assertNotNull(data.getName());
        assertEquals(data.getName(), category.getName());
        assertNotNull(data.getDescription());
        assertEquals(data.getDescription(), category.getDescription());
        List<ProductDto> products = data.getProducts();

        List<Product> all = productRepository.findAll();
        TestUtil.checkProductsEquality(products, all);
    }

    @Test
    void shouldNotFindNotExisted() {
        assertThrows(NotFoundException.class, () -> categoryService.findById(10L));
    }

    @Test
    @Transactional
    void shouldEditCategory() {

        categoryRepository.save(category);
        CategoryEditDto editDto = new CategoryEditDto(category.getId(), "new name", "description");
        ApiResult<CategoryDto> result = categoryService.edit(editDto);

        assertTrue(result.isSuccess());
        CategoryDto data = result.getData();
        assertEquals(1, categoryRepository.count());

        assertEquals(data.getId(), editDto.getId());
        assertEquals(data.getName(), editDto.getName());
        assertEquals(data.getDescription(), editDto.getDescription());

    }

    @Test
    @Transactional
    void shouldNotEditToAlreadyExistedName() {
        Category another = new Category(ANOTHER_CATEGORY_NAME, ANOTHER_CATEGORY_DESCRIPTION);
        categoryRepository.save(another);
        categoryRepository.save(category);
        CategoryEditDto editDto = new CategoryEditDto(category.getId(), another.getName(), "description");
        assertThrows(AlreadyExistsException.class, () -> categoryService.edit(editDto));
    }

    @Test
    @Transactional
    void shouldNotFoundNotExisted() {
        CategoryEditDto editDto = new CategoryEditDto(15L, "name", "description");
        assertThrows(NotFoundException.class, () -> categoryService.edit(editDto));
    }

    @Test
    @Transactional
    void shouldNotChangeProductsIfEditCategory() {

        productRepository.deleteAll();
        category.setProducts(new ArrayList<>(List.of(
                new Product("Hp Laptop", " desc ", 600.0, category),
                new Product("Acer nitro", " desc ", 600.0, category),
                new Product("Asus vivobook", " desc ", 600.0, category)
        )));
        categoryRepository.save(category);
        CategoryEditDto editDto = new CategoryEditDto(category.getId(), "new name", "description");
        ApiResult<CategoryDto> result = categoryService.edit(editDto);

        assertTrue(result.isSuccess());
        CategoryDto data = result.getData();
        assertEquals(1, categoryRepository.count());

        assertEquals(data.getId(), editDto.getId());
        assertEquals(data.getName(), editDto.getName());
        assertEquals(data.getDescription(), editDto.getDescription());

        List<ProductDto> products = data.getProducts();
        List<Product> all = productRepository.findAll();
        TestUtil.checkProductsEquality(products, all);
    }

    @Test
    @Transactional
    void shouldGetAll() {

        categoryRepository.saveAll(List.of(
                category,
                new Category("category1", "description"),
                new Category("category2", "description"),
                new Category("category3", "description")
        ));
        ApiResult<List<CategoryDto>> result = categoryService.getAll();
        assertTrue(result.isSuccess());
        List<CategoryDto> data = result.getData();
        List<Category> all = categoryRepository.findAll();
        TestUtil.checkCategoriesEquality(data, all);
    }

    @Test
    @Transactional
    void shouldGetAllForInfo() {

        categoryRepository.saveAll(new ArrayList<>(List.of(
                category,
                new Category("category1", "description"),
                new Category("category2", "description"),
                new Category("category3", "description")
        )));
        ApiResult<List<CategoryInfoDto>> result = categoryService.getAllForInfo();
        assertTrue(result.isSuccess());
        List<CategoryInfoDto> data = result.getData();
        List<Category> all = categoryRepository.findAll();
        TestUtil.checkCategoriesInfoEquality(data, all);
    }

    @Test
    void shouldDeleteById() {

        categoryRepository.saveAll(new ArrayList<>(List.of(
                category,
                new Category("category1", "description"),
                new Category("category2", "description"),
                new Category("category3", "description")
        )));

        ApiResult<Void> delete = categoryService.delete(category.getId());
        assertTrue(delete.isSuccess());
        Optional<Category> byId = categoryRepository.findById(category.getId());
        assertTrue(byId.isEmpty());

    }

    @Test
    void shouldNotDeleteByIdNotExisted() {

        categoryRepository.saveAll(new ArrayList<>(List.of(
                new Category("category1", "description"),
                new Category("category2", "description"),
                new Category("category3", "description")
        )));

        assertThrows(NotFoundException.class, () -> categoryService.delete(150L));

    }

    @Test
    void shouldDeleteCategoryAndItsProducts() {
        productRepository.deleteAll();
        category.setProducts(new ArrayList<>(List.of(
                new Product("Hp Laptop", " desc ", 600.0, category),
                new Product("Acer nitro", " desc ", 600.0, category),
                new Product("Asus vivobook", " desc ", 600.0, category)
        )));
        categoryRepository.save(category);
        ApiResult<Void> delete = categoryService.delete(category.getId());
        assertTrue(delete.isSuccess());
        assertEquals(0, productRepository.count());
    }

}

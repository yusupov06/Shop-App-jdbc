package uz.md.shopappjdbc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uz.md.shopappjdbc.service.query.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import uz.md.shopappjdbc.IntegrationTest;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.product.ProductAddDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.product.ProductEditDto;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.exceptions.AlreadyExistsException;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.repository.contract.CategoryRepository;
import uz.md.shopappjdbc.repository.contract.ProductRepository;
import uz.md.shopappjdbc.service.contract.ProductService;
import uz.md.shopappjdbc.util.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceTest {


    private static final String CATEGORY_NAME = "Laptop";
    private static final String CATEGORY_DESCRIPTION = " laptops ";

    private static final String DEFAULT_NAME = "HP";
    private static final String DEFAULT_DESCRIPTION = " hp ";
    private static final Double DEFAULT_PRICE = 500.0;

    private static final String ANOTHER_NAME = "Acer";
    private static final String ANOTHER_DESCRIPTION = " acer ";
    private static final Double ANOTHER_PRICE = 500.0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Product product;
    private Category category;

    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    public void init() {
        saveCategory();
        product = new Product();
        product.setName(DEFAULT_NAME);
        product.setDescription(DEFAULT_DESCRIPTION);
        product.setPrice(DEFAULT_PRICE);
        product.setDeleted(false);
        product.setActive(true);
        product.setCategory(category);
        productRepository.deleteAll();
    }

    private void saveCategory() {
        categoryRepository.deleteAll();
        category = new Category(CATEGORY_NAME, CATEGORY_DESCRIPTION);
        categoryRepository.save(category);
    }

    // Find by id test

    @Test
    @Transactional
    void shouldFindById() {

        productRepository.save(product);
        ApiResult<ProductDto> result = productService.findById(product.getId());

        assertTrue(result.isSuccess());
        ProductDto data = result.getData();
        assertNotNull(data.getId());
        assertEquals(data.getId(), product.getId());
        assertNotNull(data.getName());
        assertEquals(data.getName(), product.getName());
        assertNotNull(data.getDescription());
        assertEquals(data.getDescription(), product.getDescription());
        assertNotNull(data.getPrice());
        assertEquals(data.getPrice(), product.getPrice());

    }

    @Test
    @Transactional
    void shouldNotFindNotExisted() {
        assertThrows(NotFoundException.class, () -> productService.findById(15L));
    }

    // add test
    @Test
    @Transactional
    void shouldAddProduct() {

        ProductAddDto addDto = new ProductAddDto("product", "description", 500.0, category.getId());
        ApiResult<ProductDto> result = productService.add(addDto);

        assertTrue(result.isSuccess());
        List<Product> all = productRepository.findAll();
        Product product1 = all.get(0);

        assertEquals(product1.getName(), addDto.getName());
        assertEquals(product1.getDescription(), addDto.getDescription());
        assertEquals(product1.getCategory().getId(), addDto.getCategoryId());
    }

    @Test
    @Transactional
    void shouldNotAddWithAlreadyExistedName() {
        productRepository.save(product);
        ProductAddDto addDto = new ProductAddDto(product.getName(), "description", 400.0, category.getId());
        assertThrows(AlreadyExistsException.class, () -> productService.add(addDto));
    }

    @Test
    @Transactional
    void shouldNotAddWithNotExistedCategory() {
        productRepository.save(product);
        ProductAddDto addDto = new ProductAddDto("name", "description", 400.0, 20L);
        assertThrows(NotFoundException.class, () -> productService.add(addDto));
    }

    // Edit product test

    @Test
    @Transactional
    void shouldEditProduct() {

        productRepository.save(product);
        ProductEditDto editDto = new ProductEditDto(
                product.getId(),
                "new name",
                "description",
                500.0,
                category.getId());

        ApiResult<ProductDto> result = productService.edit(editDto);

        assertTrue(result.isSuccess());
        ProductDto data = result.getData();
        assertEquals(1, productRepository.count());

        assertEquals(data.getId(), editDto.getId());
        assertEquals(data.getName(), editDto.getName());
        assertEquals(data.getDescription(), editDto.getDescription());
        assertEquals(data.getPrice(), editDto.getPrice());
        assertEquals(data.getCategoryId(), editDto.getCategoryId());

    }

    @Test
    @Transactional
    void shouldNotEditToAlreadyExistedName() {
        productRepository.save(new Product(ANOTHER_NAME, ANOTHER_DESCRIPTION, ANOTHER_PRICE, category));
        productRepository.save(product);
        ProductEditDto editDto = new ProductEditDto(product.getId(), ANOTHER_NAME, "description", 500.0, category.getId());
        assertThrows(AlreadyExistsException.class, () -> productService.edit(editDto));
    }

    @Test
    @Transactional
    void shouldNotEditToNotExistedCategory() {
        productRepository.save(product);
        ProductEditDto editDto = new ProductEditDto(product.getId(), ANOTHER_NAME, "description", 500.0, 50L);
        assertThrows(NotFoundException.class, () -> productService.edit(editDto));

    }

    @Test
    @Transactional
    void shouldNotFound() {
        ProductEditDto editDto = new ProductEditDto(15L, "name", "description", 400.0, category.getId());
        assertThrows(NotFoundException.class, () -> productService.edit(editDto));
    }


    // Delete a product test

    @Test
    void shouldDeleteById() {

        productRepository.saveAll(new ArrayList<>(List.of(
                product,
                new Product("product1", "description", 500.0, category),
                new Product("product2", "description", 500.0, category),
                new Product("product3", "description", 500.0, category)
        )));

        ApiResult<Void> delete = productService.delete(product.getId());
        assertTrue(delete.isSuccess());
        Optional<Product> byId = productRepository.findById(product.getId());
        assertTrue(byId.isEmpty());

    }

    @Test
    void shouldNotDeleteByNotExistedId() {

        productRepository.saveAll(new ArrayList<>(List.of(
                new Product("product1", "description", 500.0, category),
                new Product("product2", "description", 500.0, category),
                new Product("product3", "description", 500.0, category)
        )));

        assertThrows(NotFoundException.class, () -> productService.delete(150L));

    }


    // Get all by category test

    @Test
    @Transactional
    void shouldGetAllByCategory() {

        productRepository.saveAll(new ArrayList<>(List.of(
                product,
                new Product("product1", "description", 500.0, category),
                new Product("product2", "description", 500.0, category),
                new Product("product3", "description", 500.0, category)
        )));
        ApiResult<List<ProductDto>> result = productService.getAllByCategory(category.getId());
        assertTrue(result.isSuccess());
        List<ProductDto> data = result.getData();
        List<Product> all = productRepository.findAll();
        TestUtil.checkProductsEquality(data, all);
    }

    @Test
    @Transactional
    void shouldGetAllByNotExistedCategory() {
        assertThrows(NotFoundException.class, () -> productService.getAllByCategory(15L));
    }

    // Finds all products by simple search

    @Test
    void shouldFindProductsBySimpleSearch() {
        productRepository.saveAll(new ArrayList<>(List.of(
                product,
                new Product("Hp laptop 15dy", "description", 500.0, category),
                new Product("Hp laptop 14dy", "description", 500.0, category),
                new Product("Acer laptop ", "description", 500.0, category)
        )));

        SimpleSearchRequest searchRequest = SimpleSearchRequest
                .builder()
                .fields(new String[]{"name", "description"})
                .key("hp")
                .sortBy("name")
                .sortDirection(Sort.Direction.DESC)
                .page(0)
                .pageCount(10)
                .build();

        ApiResult<List<ProductDto>> result = productService.findAllBySimpleSearch(searchRequest);

        assertTrue(result.isSuccess());
        List<ProductDto> data = result.getData();
        System.out.println("data = " + data);
        assertEquals(3, data.size());

    }

}

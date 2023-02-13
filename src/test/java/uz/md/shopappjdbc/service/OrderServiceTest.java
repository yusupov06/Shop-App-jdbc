package uz.md.shopappjdbc.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uz.md.shopappjdbc.service.query.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import uz.md.shopappjdbc.IntegrationTest;
import uz.md.shopappjdbc.domain.*;
import uz.md.shopappjdbc.domain.enums.OrderStatus;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.address.AddressAddDto;
import uz.md.shopappjdbc.dtos.order.OrderAddDto;
import uz.md.shopappjdbc.dtos.order.OrderDto;
import uz.md.shopappjdbc.dtos.order.OrderProductAddDto;
import uz.md.shopappjdbc.dtos.orderProduct.OrderProductDto;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;
import uz.md.shopappjdbc.exceptions.IllegalRequestException;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.repository.*;
import uz.md.shopappjdbc.repository.contract.*;
import uz.md.shopappjdbc.service.contract.OrderService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceTest {

    private static final OrderStatus ORDER_STATUS = OrderStatus.PREPARING;
    private static final Double OVERALL_PRICE = 800.0;

    private static final String USER_FIRST_NAME = "Ali";
    private static final String USER_LAST_NAME = "Ali";
    private static final String USER_PHONE_NUMBER = "+998931664455";
    private static final String USER_PASSWORD = "123";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private Order order;
    private User user;
    private Address address;
    private Category category;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void init() {
        order = new Order();
        order.setStatus(ORDER_STATUS);
        order.setActive(true);
        order.setDeleted(false);
        order.setOverallPrice(OVERALL_PRICE);
        user = userRepository.save(new User(
                USER_FIRST_NAME,
                USER_LAST_NAME,
                USER_PHONE_NUMBER,
                USER_PASSWORD,
                roleRepository
                        .save(new Role("ADMIN",
                                "description",
                                Set.of(PermissionEnum.values()))),
                true));
        order.setUser(user);
        address = addressRepository
                .save(new Address(user,
                        45,
                        "street",
                        "Tashkent"));
        order.setAddress(addressRepository
                .save(address));
    }

    @Test
    @Transactional
    void shouldFindById() {
        orderRepository.save(order);
        ApiResult<OrderDto> byId = orderService.findById(order.getId());
        assertTrue(byId.isSuccess());
        OrderDto data = byId.getData();

        assertEquals(data.getId(), order.getId());
        assertEquals(data.getOverallPrice(), order.getOverallPrice());
        assertEquals(data.getStatus(), order.getStatus());
        assertEquals(data.getUserId(), order.getUser().getId());
        assertEquals(data.getAddress().getId(), order.getAddress().getId());
    }

    @Test
    @Transactional
    void shouldNotFindById() {
        assertThrows(NotFoundException.class, () -> orderService.findById(15L));
    }

    @Test
    @Transactional
    void shouldAddWithUserAddedAddress() {
        Category category = categoryRepository.save(new Category("category", "description"));
        Address address1 = address;
        List<Product> products = productRepository.saveAll(List.of(
                new Product("product1", "description", 500.0, category),
                new Product("product1", "description", 500.0, category)
        ));

        List<OrderProductAddDto> orderProductAddDtos = new ArrayList<>(List.of(
                new OrderProductAddDto(products.get(0).getId(), 2, 2 * products.get(0).getPrice()),
                new OrderProductAddDto(products.get(1).getId(), 2, 2 * products.get(1).getPrice())
        ));

        OrderAddDto addDto = new OrderAddDto(user.getId(),
                null,
                address1.getId(),
                500.0,
                orderProductAddDtos);

        ApiResult<OrderDto> add = orderService.add(addDto);

        assertTrue(add.isSuccess());
        OrderDto data = add.getData();
        assertNotNull(data.getId());
        assertEquals(data.getOverallPrice(), 2000.0);
        assertEquals(data.getStatus(), OrderStatus.PREPARING);
        assertEquals(data.getUserId(), addDto.getUserId());
        checkOrderProductAndAddDtoEquals(data.getOrderProducts(), orderProductAddDtos);
        assertEquals(data.getAddress().getId(), addDto.getAddressId());
    }

    @Test
    @Transactional
    void shouldAddWithAddressAddAndOrderAdd() {
        Category category = categoryRepository.save(new Category("category", "description"));

        List<Product> products = productRepository.saveAll(List.of(
                new Product("product1", "description", 500.0, category),
                new Product("product1", "description", 500.0, category)
        ));

        List<OrderProductAddDto> orderProductAddDtos = new ArrayList<>(List.of(
                new OrderProductAddDto(products.get(0).getId(), 2, 2 * products.get(0).getPrice()),
                new OrderProductAddDto(products.get(1).getId(), 2, 2 * products.get(1).getPrice())
        ));
        AddressAddDto addressAddDto = new AddressAddDto(45, "street", "Andijan", user.getId());
        OrderAddDto addDto = new OrderAddDto(user.getId(),
                addressAddDto,
                null,
                500.0,
                orderProductAddDtos);

        ApiResult<OrderDto> add = orderService.add(addDto);

        assertTrue(add.isSuccess());
        OrderDto data = add.getData();
        assertNotNull(data.getId());
        assertEquals(data.getOverallPrice(), 2000.0);
        assertEquals(data.getStatus(), OrderStatus.PREPARING);
        assertEquals(data.getUserId(), addDto.getUserId());
        checkOrderProductAndAddDtoEquals(data.getOrderProducts(), orderProductAddDtos);
        assertNotNull(data.getAddress().getId());
        assertEquals(data.getAddress().getUserId(), addressAddDto.getUserId());
        assertEquals(data.getAddress().getHouseNumber(), addressAddDto.getHouseNumber());
        assertEquals(data.getAddress().getCity(), addressAddDto.getCity());
        assertEquals(data.getAddress().getStreet(), addressAddDto.getStreet());
    }

    private void checkOrderProductAndAddDtoEquals(List<OrderProductDto> actual, List<OrderProductAddDto> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            OrderProductDto dto = actual.get(i);
            OrderProductAddDto addDto = expected.get(i);
            assertEquals(addDto.getQuantity(), dto.getQuantity());
            assertEquals(addDto.getProductId(), dto.getProduct().getId());
            assertEquals(addDto.getPrice(), dto.getPrice());
        }
    }

    @Test
    @Transactional
    void shouldNotAddOrderWithOutUserId() {

        Category category = categoryRepository.save(new Category("category", "description"));

        List<Product> products = productRepository.saveAll(List.of(
                new Product("product1", "description", 500.0, category),
                new Product("product1", "description", 500.0, category)
        ));

        List<OrderProductAddDto> orderProductAddDtos = new ArrayList<>(List.of(
                new OrderProductAddDto(products.get(0).getId(), 2, 2 * products.get(0).getPrice()),
                new OrderProductAddDto(products.get(1).getId(), 2, 2 * products.get(1).getPrice())
        ));
        AddressAddDto addressAddDto = new AddressAddDto(45, "street", "Andijan", user.getId());
        OrderAddDto addDto = new OrderAddDto(
                UUID.randomUUID(),
                addressAddDto,
                null,
                500.0,
                orderProductAddDtos);

        assertThrows(NotFoundException.class, () -> orderService.add(addDto));
    }

    @Test
    @Transactional
    void shouldNotAddOrderWithOutAddress() {

        Category category = categoryRepository.save(new Category("category", "description"));

        List<Product> products = productRepository.saveAll(List.of(
                new Product("product1", "description", 500.0, category),
                new Product("product1", "description", 500.0, category)
        ));

        List<OrderProductAddDto> orderProductAddDtos = new ArrayList<>(List.of(
                new OrderProductAddDto(products.get(0).getId(), 2, 2 * products.get(0).getPrice()),
                new OrderProductAddDto(products.get(1).getId(), 2, 2 * products.get(1).getPrice())
        ));
        OrderAddDto addDto = new OrderAddDto(
                user.getId(),
                null,
                45L,
                500.0,
                orderProductAddDtos);

        assertThrows(NotFoundException.class, () -> orderService.add(addDto));
    }

    @Test
    @Transactional
    void shouldNotAddOrderWithOutAddress2() {

        Category category = categoryRepository.save(new Category("category", "description"));

        List<Product> products = productRepository.saveAll(List.of(
                new Product("product1", "description", 500.0, category),
                new Product("product1", "description", 500.0, category)
        ));

        List<OrderProductAddDto> orderProductAddDtos = new ArrayList<>(List.of(
                new OrderProductAddDto(products.get(0).getId(), 2, 2 * products.get(0).getPrice()),
                new OrderProductAddDto(products.get(1).getId(), 2, 2 * products.get(1).getPrice())
        ));
        OrderAddDto addDto = new OrderAddDto(
                user.getId(),
                null,
                null,
                500.0,
                orderProductAddDtos);

        assertThrows(IllegalRequestException.class, () -> orderService.add(addDto));
    }

    @Test
    @Transactional
    void shouldDelete() {
        orderRepository.save(order);
        ApiResult<Void> delete = orderService.delete(order.getId());
        assertTrue(delete.isSuccess());
    }

    @Test
    void shouldNotDeleteWithNotExistedId() {
        assertThrows(NotFoundException.class, () -> orderService.delete(10L));
    }

    @Test
    void shouldGetAllByPage() {
        List<Order> orders = generateOrders(10);
        orderRepository.saveAll(orders);
//        generateOrderProductsForOrders(orders, 3);
        ApiResult<List<OrderDto>> allByPage = orderService.getAllByPage("0-5");
        assertTrue(allByPage.isSuccess());
        List<OrderDto> data = allByPage.getData();
        assertEquals(5, data.size());
    }

    @Test
    void shouldGetBySort(){
        List<Order> orders = generateOrders(10);
        orderRepository.saveAll(orders);
        SimpleSortRequest sortRequest = SimpleSortRequest.builder()
                .sortBy("user")
                .direction(Sort.Direction.ASC)
                .page(0)
                .pageCount(4)
                .build();

        ApiResult<List<OrderDto>> allBySort = orderService.findAllBySort(sortRequest);

        assertTrue(allBySort.isSuccess());
        List<OrderDto> data = allBySort.getData();
        assertEquals(4, data.size());
    }

    private void generateOrderProductsForOrders(List<Order> orders, int count) {
        List<Product> products = generateProduct(orders.size() * count);
        int k = 0;
        for (Order o : orders) {
            List<OrderProduct> orderProducts = new ArrayList<>();
            for (int i = 0; i < count; i++){
                orderProducts.add(OrderProduct
                        .builder()
                        .order(o)
                        .product(products.get(k))
                        .price(products.get(k).getPrice() * 2)
                        .quantity(2)
                        .deleted(false)
                        .build());
                k++;
            }
            o.setOrderProducts(orderProducts);
        }
    }

    private List<Product> generateProduct(int count) {

        List<Product> products = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            double d = 500.0 * random.nextDouble() + 100.0;
            products.add(Product.builder()
                    .name("product" + i)
                    .description("product description")
                    .price(d)
                    .category(category)
                    .build());
        }
        return products;
    }


    private List<Order> generateOrders(int count) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orders.add(Order.builder()
                    .user(user)
                    .address(address)
                    .status(OrderStatus.PREPARING)
                    .build());
        }
        return orders;
    }

}

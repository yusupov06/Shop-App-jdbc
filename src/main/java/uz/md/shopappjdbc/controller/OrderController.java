package uz.md.shopappjdbc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.aop.annotation.CheckAuth;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.order.OrderAddDto;
import uz.md.shopappjdbc.dtos.order.OrderDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;
import uz.md.shopappjdbc.service.contract.OrderService;
import uz.md.shopappjdbc.utils.AppConstants;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(OrderController.BASE_URL + "/")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    public static final String BASE_URL = AppConstants.BASE_URL + "order";

    private final OrderService orderService;

    /**
     * Gets the order by its id
     *
     * @param id order's id
     * @return found order
     */
    @GetMapping("/by_id/{id}")
    @CheckAuth(permission = PermissionEnum.GET_ORDER)
    public ApiResult<OrderDto> getById(@PathVariable Long id) {
        log.info("Getting by id {}", id);
        return orderService.findById(id);
    }

    /**
     * Adds a new order
     *
     * @param dto for adding new order
     * @return newly added order
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @CheckAuth(permission = PermissionEnum.ADD_ORDER)
    public ApiResult<OrderDto> add(@RequestBody @Valid OrderAddDto dto) {
        log.info("adding order");
        log.info("Request body: {}", dto);
        return orderService.add(dto);
    }

    /**
     * deletes an order
     *
     * @param id order's id
     * @return success if deleted or failure otherwise
     */
    @DeleteMapping("/delete/{id}")
    @CheckAuth(permission = PermissionEnum.DELETE_ORDER)
    public ApiResult<Void> delete(@PathVariable Long id) {
        log.info("delete order by id {}", id);
        return orderService.delete(id);
    }

    /**
     * gets orders by pagination
     *
     * @param pagination 0-15
     * @return List of orders
     */
    @GetMapping("/by_page/{pagination}")
    @CheckAuth(permission = PermissionEnum.GET_ORDER)
    public ApiResult<List<OrderDto>> getAllByPage(@PathVariable String pagination) {
        log.info("getting all orders by pagination: {}", pagination);
        return orderService.getAllByPage(pagination);
    }


    /**
     * gets orders by status
     *
     * @param status order's status
     * @return List of orders
     */
    @GetMapping("/status/{status}/{pagination}")
    @ApiResponse(description = "Getting orders by status")
    @CheckAuth(permission = PermissionEnum.GET_ORDER)
    public ApiResult<List<OrderDto>> getOrdersByStatus(@PathVariable String status, @PathVariable String pagination) {
        log.info("Getting orders by status: {}", status);
        return orderService.getOrdersByStatus(status, pagination);
    }

    /**
     * gets orders by userId
     *
     * @param userId order's userId
     * @return List of orders
     */
    @GetMapping("/user/{userId}/{pagination}")
    @ApiResponse(description = "Getting orders by userId")
    @CheckAuth(permission = PermissionEnum.GET_ORDER)
    public ApiResult<List<OrderDto>> getOrdersByUserId(@PathVariable UUID userId, @PathVariable String pagination) {
        log.info("getOrders by userId {} ", userId);
        return orderService.getOrdersByUserId(userId, pagination);
    }

    /**
     * Simple Sorting for orders
     *
     * @param request sorting orders dto
     * @return List of orders sorted
     */
    @PostMapping("/sorting")
    @ApiResponse(description = "List of orders sorted")
    @CheckAuth(permission = PermissionEnum.GET_ORDER)
    public ApiResult<List<OrderDto>> getOrdersBySort(@RequestBody @Valid SimpleSortRequest request) {
        log.info("Get orders by sort request");
        log.info("Request body {}", request);
        return orderService.findAllBySort(request);
    }

    @PostMapping("/filter")
    @Operation(description = "List of categories")
    @CheckAuth(permission = PermissionEnum.GET_ORDER)
    public ApiResult<List<OrderDto>> getOrdersByFilter(@RequestBody @Valid FilterRequest request) {
        log.info("getProductsBySort");
        log.info("Request body is: {}", request);
        return orderService.findAllByFilter(request);
    }


}

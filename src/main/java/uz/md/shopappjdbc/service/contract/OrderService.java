package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.order.OrderAddDto;
import uz.md.shopappjdbc.dtos.order.OrderDto;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    /**
     * finds order by id
     *
     * @param id order's id
     * @return the order
     */
    ApiResult<OrderDto> findById(Long id);

    /**
     * Adds a new order
     * @param dto for adding the order
     * @return newly added order
     */
    ApiResult<OrderDto> add(OrderAddDto dto);

    /**
     * Deletes order by id
     * @param id order id
     * @return success if deleted successfuly or else otherwise
     */
    ApiResult<Void> delete(Long id);

    /**
     * gets orders by page
     * @param pagination pagination
     * @return orders by pagination
     */
    ApiResult<List<OrderDto>> getAllByPage(String pagination);

    /**
     * gets orders by sort
     * @param request sort request
     * @return sorted list of orders
     */
    ApiResult<List<OrderDto>> findAllBySort(SimpleSortRequest request);


    /**
     * gets orders by status
     * @param status
     * @param pagination
     * @return found list of products
     */
    ApiResult<List<OrderDto>> getOrdersByStatus(String status, String pagination);

    /**
     * gets orders by userId
     * @param userid id of user
     * @param pagination
     * @return list of products
     */
    ApiResult<List<OrderDto>> getOrdersByUserId(UUID userid, String pagination);

    ApiResult<List<OrderDto>> findAllByFilter(FilterRequest request);
}

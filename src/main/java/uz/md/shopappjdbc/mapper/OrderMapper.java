package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.md.shopappjdbc.domain.Order;
import uz.md.shopappjdbc.dtos.order.OrderAddDto;
import uz.md.shopappjdbc.dtos.order.OrderDto;
import uz.md.shopappjdbc.service.contract.UserService;

@Mapper(componentModel = "spring",
        uses = {AddressMapper.class,
                OrderMapper.class,
                OrderProductMapper.class})
public interface OrderMapper extends EntityMapper<Order, OrderDto> {

    Order fromAddDto(OrderAddDto dto);

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "address", expression = " java( addressMapper.toDto(entity.getAddress()) ) ")
    @Mapping(target = "orderProducts", expression = " java( orderProductMapper.toDtoList(entity.getOrderProducts()) )")
    OrderDto toDto(Order entity);
}

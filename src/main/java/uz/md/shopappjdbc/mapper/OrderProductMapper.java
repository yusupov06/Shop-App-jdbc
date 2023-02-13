package uz.md.shopappjdbc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.md.shopappjdbc.domain.OrderProduct;
import uz.md.shopappjdbc.dtos.order.OrderProductAddDto;
import uz.md.shopappjdbc.dtos.orderProduct.OrderProductDto;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderProductMapper extends EntityMapper<OrderProduct, OrderProductDto> {

    OrderProduct fromAddDto(OrderProductAddDto addDto);

    @Override
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "product", expression = " java( productMapper.toDto(entity.getProduct()) ) ")
    OrderProductDto toDto(OrderProduct entity);

}

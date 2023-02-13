package uz.md.shopappjdbc.mapper;

import java.util.List;

public interface EntityMapper<E, D> {

    D toDto(E entity);

    List<D> toDtoList(List<E> entities);

    E fromDto(D dto);

    List<E> fromDtoList(List<D> dtoList);
}

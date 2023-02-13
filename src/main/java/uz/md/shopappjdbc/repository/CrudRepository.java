package uz.md.shopappjdbc.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CrudRepository<T, ID> {
    T save(T entity);

    List<T> saveAll(List<T> entities);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    List<T> findAll();

    List<T> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(T entity);

    void deleteAllById(Iterable<? extends ID> ids);

    void deleteAll(Iterable<? extends T> entities);

    void deleteAll();
}

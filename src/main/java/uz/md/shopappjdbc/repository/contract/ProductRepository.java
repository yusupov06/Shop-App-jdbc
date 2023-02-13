package uz.md.shopappjdbc.repository.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;
import uz.md.shopappjdbc.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.md.shopappjdbc.domain.Product;

import java.net.ContentHandler;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);

    List<Product> findAllByCategory_Id(Long category_id);

    Page<Product> findAll(PageRequest pageRequest);

    Page<Product> execute(String query, Pageable pageable);
}

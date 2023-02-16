package uz.md.shopappjdbc.repository.contract;

import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends CrudRepository<Category, Long> {

    List<CategoryInfoDto> findAllForInfo();

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);
}

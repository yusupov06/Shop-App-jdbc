package uz.md.shopappjdbc.repository.contract;

import uz.md.shopappjdbc.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.md.shopappjdbc.domain.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {

    boolean existsByNameIgnoreCase(String name);

    List<Role> findAllByIdIsNot(Integer id);

    Optional<Role> findByName(String name);

}

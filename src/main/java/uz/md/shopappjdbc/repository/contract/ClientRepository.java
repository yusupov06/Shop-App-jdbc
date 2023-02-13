package uz.md.shopappjdbc.repository.contract;

import uz.md.shopappjdbc.repository.CrudRepository;
import uz.md.shopappjdbc.domain.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {
    boolean existsByUsername(String username);
}

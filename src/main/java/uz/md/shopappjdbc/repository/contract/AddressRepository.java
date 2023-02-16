package uz.md.shopappjdbc.repository.contract;

import uz.md.shopappjdbc.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.md.shopappjdbc.domain.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends CrudRepository<Address, Long> {
    Optional<Address> findByIdAndUserId(Long id, UUID user_id);

    List<Address> findAllByUserId(UUID userId);
}

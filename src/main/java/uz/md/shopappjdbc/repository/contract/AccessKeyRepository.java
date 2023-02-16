package uz.md.shopappjdbc.repository.contract;

import org.springframework.data.repository.NoRepositoryBean;
import uz.md.shopappjdbc.repository.CrudRepository;
import uz.md.shopappjdbc.domain.AccessKey;

import java.util.List;
import java.util.Optional;

public interface AccessKeyRepository extends CrudRepository<AccessKey, Long> {
    Optional<AccessKey> findByAccess(String access);

    List<AccessKey> findAllByClientId(Long id);
}

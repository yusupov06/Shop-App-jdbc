package uz.md.shopappjdbc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import uz.md.shopappjdbc.IntegrationTest;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.role.RoleAddDTO;
import uz.md.shopappjdbc.dtos.role.RoleDTO;
import uz.md.shopappjdbc.exceptions.AlreadyExistsException;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.service.contract.RoleService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static uz.md.shopappjdbc.domain.enums.PermissionEnum.ADD_PRODUCT;

@IntegrationTest
@ActiveProfiles("test")
@Transactional
public class RoleServiceTest {

    private static final String NAME = "ADMIN";

    private static final String DESCRIPTION = " sys admin ";

    private static final String ANOTHER_NAME = "Yusupov";
    private static final String ANOTHER_DESCRIPTION = "123";


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    private Role role;

    @BeforeEach
    public void init() {
        role = new Role();
        role.setName(NAME);
        role.setDescription(DESCRIPTION);
        role.setActive(true);
        role.setDeleted(false);
        role.setPermissions(Set.of(PermissionEnum.values()));
        roleRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAdd() {
        RoleAddDTO roleAddDTO = new RoleAddDTO(ANOTHER_NAME, ANOTHER_DESCRIPTION, Set.of(ADD_PRODUCT));
        ApiResult<RoleDTO> add = roleService.add(roleAddDTO);
        assertTrue(add.isSuccess());
        RoleDTO data = add.getData();
        assertEquals(data.getName(), roleAddDTO.getName());
        assertEquals(data.getDescription(), roleAddDTO.getDescription());
        assertEquals(data.getPermissions(), roleAddDTO.getPermissions());

    }

    @Test
    @Transactional
    void shouldNotAddWithAlreadyExistedName() {
        roleRepository.save(role);
        RoleAddDTO roleAddDTO = new RoleAddDTO(role.getName(), ANOTHER_DESCRIPTION, Set.of(ADD_PRODUCT));
        assertThrows(AlreadyExistsException.class, () -> roleService.add(roleAddDTO));
    }



}

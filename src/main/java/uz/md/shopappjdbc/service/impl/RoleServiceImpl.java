package uz.md.shopappjdbc.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uz.md.shopappjdbc.domain.Role;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.role.RoleAddDTO;
import uz.md.shopappjdbc.dtos.role.RoleDTO;
import uz.md.shopappjdbc.dtos.role.RoleEditDTO;
import uz.md.shopappjdbc.exceptions.AlreadyExistsException;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.mapper.RoleMapper;
import uz.md.shopappjdbc.repository.contract.RoleRepository;
import uz.md.shopappjdbc.service.contract.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final MessageSource messageSource;

    public RoleServiceImpl(RoleRepository roleRepository,
                           RoleMapper roleMapper,
                           MessageSource messageSource) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.messageSource = messageSource;
    }

    @Override
    public ApiResult<RoleDTO> add(RoleAddDTO dto) {
        if (roleRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new AlreadyExistsException("ROLE_NAME_ALREADY_EXISTS");
        }
        Role role = roleMapper.fromAddDTO(dto);
        return ApiResult
                .successResponse(roleMapper
                        .toDto(roleRepository.save(role)));
    }

    @Override
    public ApiResult<List<RoleDTO>> getAll() {
        return ApiResult
                .successResponse(roleMapper
                        .toDtoList(roleRepository
                                .findAll()));
    }

    @Override
    public ApiResult<RoleDTO> getById(Integer id) {
        return ApiResult
                .successResponse(roleMapper
                        .toDto(roleRepository
                                .findById(id)
                                .orElseThrow(() -> new NotFoundException("ROLE_NOT_FOUND_WITH_ID"))));
    }

    @Override
    public ApiResult<RoleDTO> edit(RoleEditDTO dto) {

        Role role = roleRepository
                .findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("ROLE_NOT_FOUND_WITH_ID"));

        Role role1 = roleMapper.fromEditDto(role, dto);
        return ApiResult
                .successResponse(roleMapper
                        .toDto(roleRepository.save(role1)));
    }

    @Override
    public ApiResult<Boolean> delete(Integer id, Integer insteadOfRoleId) {
        return null;
    }
}

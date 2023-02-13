package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.role.RoleAddDTO;
import uz.md.shopappjdbc.dtos.role.RoleDTO;
import uz.md.shopappjdbc.dtos.role.RoleEditDTO;

import java.util.List;

public interface RoleService {

    ApiResult<RoleDTO> add(RoleAddDTO roleAddDTO);

    ApiResult<List<RoleDTO>> getAll();

    ApiResult<RoleDTO> getById(Integer id);

    ApiResult<RoleDTO> edit(RoleEditDTO dto);

    ApiResult<Boolean> delete(Integer id, Integer insteadOfRoleId);

}

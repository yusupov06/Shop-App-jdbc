package uz.md.shopappjdbc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.aop.annotation.CheckAuth;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.role.RoleAddDTO;
import uz.md.shopappjdbc.dtos.role.RoleDTO;
import uz.md.shopappjdbc.dtos.role.RoleEditDTO;
import uz.md.shopappjdbc.service.contract.RoleService;
import uz.md.shopappjdbc.utils.AppConstants;

import java.util.List;

@RestController
@RequestMapping(value = RoleController.BASE_URL)
@RequiredArgsConstructor
public class RoleController {

    public static final String BASE_URL = AppConstants.BASE_URL + "role/";
    private final RoleService roleService;

    /**
     * Adds a role
     * @param roleAddDTO role adding params
     * @return added role
     */
    @PostMapping("add")
    @ResponseStatus(HttpStatus.CREATED)
    @CheckAuth(permission = PermissionEnum.ADD_ROLE)
    public ApiResult<RoleDTO> add(@RequestBody @Valid RoleAddDTO roleAddDTO) {
        return roleService.add(roleAddDTO);
    }

    /**
     * Gets all roles
     * @return list of roles
     */
    @GetMapping("all")
    @CheckAuth(permission = PermissionEnum.GET_ROLE)
    public ApiResult<List<RoleDTO>> getAll() {
        return roleService.getAll();
    }

    /**
     * gets a role by its id
     * @param id role id
     * @return found role
     */
    @GetMapping("{id}")
    @CheckAuth(permission = PermissionEnum.GET_ROLE)
    public ApiResult<RoleDTO> getById(@PathVariable Integer id) {
        return roleService.getById(id);
    }

    /**
     * Edits the role
     * @param dto role id and editing parameters
     * @return edited role
     */
    @PutMapping("edit")
    @ResponseStatus(HttpStatus.CREATED)
    @CheckAuth(permission = PermissionEnum.EDIT_ROLE)
    public ApiResult<RoleDTO> edit(@RequestBody @Valid RoleEditDTO dto) {
        return roleService.edit(dto);
    }

    /**
     * deletes the role
     * @param id deleting role id
     * @param insteadOfRoleId after deletion of role instead of it this role puts
     * @return true if role was deleted successfully or else false
     */
    @DeleteMapping("delete")
    @CheckAuth(permission = PermissionEnum.DELETE_ROLE)
    public ApiResult<Boolean> delete(@RequestParam Integer id, @RequestParam Integer insteadOfRoleId) {
        return roleService.delete(id,insteadOfRoleId);
    }


}

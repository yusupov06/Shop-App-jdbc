package uz.md.shopappjdbc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.md.shopappjdbc.aop.annotation.CheckAuth;
import uz.md.shopappjdbc.domain.enums.PermissionEnum;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.user.UserDto;
import uz.md.shopappjdbc.service.contract.UserService;
import uz.md.shopappjdbc.utils.AppConstants;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(UserController.BASE_URL + "/")
@RequiredArgsConstructor
public class UserController {

    public static final String BASE_URL = AppConstants.BASE_URL + "user";

    private final UserService userService;

    /**
     * Gets user by id
     * @param id finding user id
     * @return found user
     */
    @GetMapping("/{id}")
    @CheckAuth(permission = PermissionEnum.GET_USER)
    public ApiResult<UserDto> getById(@PathVariable UUID id){
        return userService.findById(id);
    }

    /**
     * deletes the specified user
     * @param id deleting user id
     * @return success if successfully deleted or else failure
     */
    @DeleteMapping("/delete/{id}")
    @CheckAuth(permission = PermissionEnum.DELETE_USER)
    public ApiResult<Void> delete(@PathVariable UUID id){
        return userService.delete(id);
    }

    @GetMapping("/me")
    public ApiResult<UserDto> me(){
        return userService.me();
    }

    @GetMapping("/all")
    public ApiResult<List<UserDto>> getAll() {
        return userService.findAll();
    }

}

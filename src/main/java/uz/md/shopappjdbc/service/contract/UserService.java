package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.user.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    ApiResult<UserDto> findById(UUID id);

    ApiResult<Void> delete(UUID id);

    ApiResult<UserDto> me();

    ApiResult<List<UserDto>> findAll();

}

package uz.md.shopappjdbc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.md.shopappjdbc.domain.User;
import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.user.UserDto;
import uz.md.shopappjdbc.exceptions.NotFoundException;
import uz.md.shopappjdbc.mapper.UserMapper;
import uz.md.shopappjdbc.repository.contract.UserRepository;
import uz.md.shopappjdbc.service.contract.UserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ApiResult<UserDto> findById(UUID id) {
        return ApiResult.successResponse(userMapper.toDto(userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"))));
    }

    @Override
    public ApiResult<Void> delete(UUID id) {
        userRepository.deleteById(id);
        return ApiResult.successResponse();
    }

    @Override
    public ApiResult<UserDto> me() {

        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        if (Objects.isNull(phoneNumber))
            throw new NotFoundException("USER_NOT_FOUND");

        User user1 = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        return ApiResult.successResponse(userMapper.toDto(user1));
    }

    @Override
    public ApiResult<List<UserDto>> findAll() {
        return ApiResult.successResponse(userMapper
                .toDtoList(userRepository.findAll()));
    }
}

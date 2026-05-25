package com.marcuswhocodes.user_service.service;

import com.marcuswhocodes.user_service.domain.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(UUID id);
    void deleteUserById(UUID id);
    UserDto updateUser(UserDto userDto);

    List<UserDto> getAllUsers();
}

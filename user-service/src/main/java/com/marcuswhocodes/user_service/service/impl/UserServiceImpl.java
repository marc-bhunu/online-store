package com.marcuswhocodes.user_service.service.impl;

import com.marcuswhocodes.user_service.domain.dto.AddressDto;
import com.marcuswhocodes.user_service.domain.dto.UserDto;
import com.marcuswhocodes.user_service.domain.entity.Address;
import com.marcuswhocodes.user_service.domain.entity.User;
import com.marcuswhocodes.user_service.exceptions.UserNotFoundException;
import com.marcuswhocodes.user_service.repository.UserRepository;
import com.marcuswhocodes.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = User.builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .phone(userDto.getPhone())
                .addresses(new ArrayList<>())
                .status(userDto.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        List<Address> address = userDto.getAddresses().stream().map((
                addressDto -> Address.builder()
                        .user(user)
                        .type(addressDto.getType())
                        .line1(addressDto.getLine1())
                        .line2(addressDto.getLine2())
                        .city(addressDto.getCity())
                        .state(addressDto.getState())
                        .zip(addressDto.getZip())
                        .country(addressDto.getCountry())
                        .isDefault(addressDto.getIsDefault())
                        .build()
        )).toList();
        user.setAddresses(address);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
    User user = userRepository.findUserByEmail(userDto.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userDto.getEmail()));
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setStatus(userDto.getStatus());
        user.setUpdatedAt(LocalDateTime.now());

        Address address = Address.builder()
                .user(user)
                .type(userDto.getAddresses().getFirst().getType())
                .line1(userDto.getAddresses().getFirst().getLine1())
                .line2(userDto.getAddresses().getFirst().getLine2())
                .city(userDto.getAddresses().getFirst().getCity())
                .state(userDto.getAddresses().getFirst().getState())
                .zip(userDto.getAddresses().getFirst().getZip())
                .country(userDto.getAddresses().getFirst().getCountry())
                .isDefault(userDto.getAddresses().getFirst().getIsDefault())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user.getAddresses().clear();
        user.getAddresses().add(address);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    private UserDto mapToDto(User savedUser) {
        return UserDto.builder()
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phone(savedUser.getPhone())
                .status(savedUser.getStatus())
                .addresses(savedUser.getAddresses().stream().map(address -> AddressDto.builder()
                        .type(address.getType())
                        .line1(address.getLine1())
                        .line2(address.getLine2())
                        .city(address.getCity())
                        .state(address.getState())
                        .zip(address.getZip())
                        .country(address.getCountry())
                        .isDefault(address.getIsDefault())
                        .build())
                        .toList())
                .build();
    }
}

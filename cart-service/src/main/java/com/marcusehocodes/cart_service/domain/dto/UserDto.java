package com.marcusehocodes.cart_service.domain.dto;

import com.marcusehocodes.cart_service.domain.enums.UserStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private List<AddressDto> addresses;
    private UserStatus status;
}
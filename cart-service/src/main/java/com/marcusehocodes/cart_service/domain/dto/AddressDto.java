package com.marcusehocodes.cart_service.domain.dto;

import com.marcusehocodes.cart_service.domain.enums.AddressType;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private AddressType type;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private Boolean isDefault;
}

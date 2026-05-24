package com.marcuswhocodes.orders_service.domain.dtos.order;

import com.marcuswhocodes.orders_service.domain.enums.AddressType;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderAddressDto {
    private AddressType type;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zip;
    private String country;

}

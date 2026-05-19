package com.marcuswhocodes.orders_service.domain.entity;


import com.marcuswhocodes.orders_service.domain.enums.AddressType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.UUID;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressSnapshot {
    private UUID id;
    @Enumerated(value = EnumType.STRING)
    private AddressType type;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zip;
    private String country;
}
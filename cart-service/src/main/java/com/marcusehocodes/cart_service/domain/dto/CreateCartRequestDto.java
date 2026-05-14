package com.marcusehocodes.cart_service.domain.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCartRequestDto {
    private UUID userId;
    private List<LineItemDto> lineItems;
    private String currency;
}

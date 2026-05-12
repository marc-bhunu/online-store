package com.marcusehocodes.cart_service.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {
    private UUID userId;
    private List<LineItemDto> lineItems;
    private Long totalPrice;
    private String currency;
}

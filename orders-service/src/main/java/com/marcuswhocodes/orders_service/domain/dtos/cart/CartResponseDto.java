package com.marcuswhocodes.orders_service.domain.dtos.cart;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDto {
    private UUID userId;
    private List<LineItemDto> lineItems;
    private Long totalPrice;
    private String currency;
}

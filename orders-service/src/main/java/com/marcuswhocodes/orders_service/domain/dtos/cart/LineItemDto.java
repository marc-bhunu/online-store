package com.marcuswhocodes.orders_service.domain.dtos.cart;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineItemDto {
    private UUID productId;
    private String productName;
    private Long price;
    private Integer quantity;
}

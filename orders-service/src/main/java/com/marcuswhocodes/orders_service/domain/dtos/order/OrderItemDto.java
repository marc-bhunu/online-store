package com.marcuswhocodes.orders_service.domain.dtos.order;



import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDto {
    private UUID productId;
    private String productName;
    private Long price;
    private Integer quantity;
    private Long subtotal;
}

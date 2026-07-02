package com.marcuswhocodes.orders_service.domain.dtos.order;


import com.marcuswhocodes.orders_service.domain.enums.OrderStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderDto {
    private UUID userId;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
    private Long totalAmount;
    private String currency;
}

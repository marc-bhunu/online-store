package com.marcuswhocodes.orders_service.domain.dtos.order;

import com.marcuswhocodes.orders_service.domain.enums.OrderStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDto {
    private UUID userId;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
    private OrderAddressDto orderAddress;
    private Long totalAmount;
    private String currency;
}

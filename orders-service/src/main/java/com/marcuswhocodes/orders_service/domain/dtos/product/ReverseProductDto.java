package com.marcuswhocodes.orders_service.domain.dtos.product;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReverseProductDto {
    private UUID productId;
    private Integer quantity;
}

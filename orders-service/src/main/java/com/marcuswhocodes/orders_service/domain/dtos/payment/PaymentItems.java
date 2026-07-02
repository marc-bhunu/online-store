package com.marcuswhocodes.orders_service.domain.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentItems {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;
}

package com.marcuswhocodes.payments.domain.dto;

import lombok.*;

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

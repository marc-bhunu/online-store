package com.marcuswhocodes.orders_service.domain.dtos.payment;


import com.marcuswhocodes.orders_service.domain.enums.PaymentMethod;
import com.marcuswhocodes.orders_service.domain.enums.PaymentStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private UUID orderId;
    private UUID userId;
    private String idempotencyKey;
    private String sessionId;
    private Long totalAmount;
    private String currency;
    private List<PaymentItems> paymentItems;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
}

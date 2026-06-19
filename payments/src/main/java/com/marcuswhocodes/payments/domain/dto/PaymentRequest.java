package com.marcuswhocodes.payments.domain.dto;

import com.marcuswhocodes.payments.domain.enums.PaymentMethod;
import com.marcuswhocodes.payments.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
//    private Long amount;
//    private Long quantity;
//    private String name;
//    private String currency;

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

package com.marcuswhocodes.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentRequestEvent(
        UUID userId,
        String idempotencyKey,
        UUID orderId,
        String paymentMethod,
        double amount,
        String currency,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp
) {
}

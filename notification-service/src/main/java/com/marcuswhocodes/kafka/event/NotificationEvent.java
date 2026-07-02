package com.marcuswhocodes.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marcuswhocodes.notificationservice.domain.enums.OrderStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NotificationEvent(
        UUID orderId,
        String email,
        OrderStatus status,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp
) {
}


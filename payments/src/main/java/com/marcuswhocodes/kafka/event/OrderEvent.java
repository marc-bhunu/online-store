package com.marcuswhocodes.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderEvent(
        UUID orderId,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp
) {
}

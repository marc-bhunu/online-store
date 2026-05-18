package com.marcuswhocodes.orderingestionservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderIngestionDto(UUID userId,
                                UUID cartId,
                                @JsonFormat(shape = JsonFormat.Shape.STRING)
                                Instant timestamp) {
}

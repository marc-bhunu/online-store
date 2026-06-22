package com.marcuswhocodes.productservice.domain.dtos;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReverseProductDto {
    private UUID productId;
    private Integer quantity;
}

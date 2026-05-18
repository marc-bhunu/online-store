package com.marcuswhocodes.productservice.domain.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InventoryDto {
    private int quantity;
    private int quantityReserved;
    private int quantityAvailable;
}

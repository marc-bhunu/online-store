package com.marcusehocodes.cart_service.domain.documents;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineItem {
    private UUID productId;
    private String productName;
    private Long price;
    private Integer quantity;
}

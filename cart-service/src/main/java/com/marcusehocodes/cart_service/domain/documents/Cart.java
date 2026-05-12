package com.marcusehocodes.cart_service.domain.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "carts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    @Id
    private String id;
    private UUID userId;
    private List<LineItem> lineItems;
    private Long totalPrice;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

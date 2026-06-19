package com.marcuswhocodes.payments.domain.entity;

import com.marcuswhocodes.payments.domain.enums.PaymentMethod;
import com.marcuswhocodes.payments.domain.enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
@Entity
@Getter
@Setter
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private String idempotencyKey;
    private String sessionId;
    private Long totalAmount;
    private String currency;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payment")
    private List<PaymentItem> paymentItems = new ArrayList<>();
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}

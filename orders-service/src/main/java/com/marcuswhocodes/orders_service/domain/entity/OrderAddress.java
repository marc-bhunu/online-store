package com.marcuswhocodes.orders_service.domain.entity;


import com.marcuswhocodes.orders_service.domain.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_addresses")
@Entity
@ToString
public class OrderAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Enumerated(value = EnumType.STRING)
    private AddressType type;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zip;
    private String country;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    private void onPersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

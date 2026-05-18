package com.marcuswhocodes.productservice.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    private int quantityReserved;
    private int quantityAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @PrePersist
    private void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Inventory that = (Inventory) o;
        return quantity == that.quantity && quantityReserved == that.quantityReserved && quantityAvailable == that.quantityAvailable && Objects.equals(id, that.id) && Objects.equals(product, that.product) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, quantity, quantityReserved, quantityAvailable, createdAt, updatedAt);
    }
}

package com.marcuswhocodes.notificationservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID orderId;
    private String email;
    private String message;
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

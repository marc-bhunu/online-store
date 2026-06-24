package com.marcuswhocodes.notificationservice.repository;

import com.marcuswhocodes.notificationservice.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SentNotificationRepository extends JpaRepository<Notification, UUID> {
}

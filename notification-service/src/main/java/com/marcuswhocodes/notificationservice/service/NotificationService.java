package com.marcuswhocodes.notificationservice.service;

import com.marcuswhocodes.kafka.event.NotificationEvent;
import com.marcuswhocodes.notificationservice.domain.entity.Notification;
import com.marcuswhocodes.notificationservice.repository.SentNotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SentNotificationRepository sentNotificationRepository;
    private final EmailNotification emailNotification;
    @KafkaListener(topics = "notifications", groupId = "notification-service-group")
    public void sendNotification(NotificationEvent notificationEvent) throws MessagingException {
        log.info("Received notification event: {}", notificationEvent);
        var message = getMessage(notificationEvent);
        emailNotification.sendNotificationEmail(notificationEvent.email(), "Online Store Notification", message);
        Notification notification = Notification.builder()
                .orderId(notificationEvent.orderId())
                .email(notificationEvent.email())
                .message(message)
                .build();
        sentNotificationRepository.save(notification);
    }

    private static @Nullable String getMessage(NotificationEvent notificationEvent) {
        String message = null;
        switch (notificationEvent.status()) {
            case CREATED -> message = "Your order has been created successfully!";
            case CONFIRMED -> message ="Your order has been confirmed and is being processed!";
            case SHIPPED -> message = "Your order has been shipped and is on its way!";
            case DELIVERED -> message = "Your order has been delivered. We hope you enjoy your purchase!";
            case CANCELLED -> message = "Your order has been cancelled. If you have any questions, please contact our support team.";
        }
        return message;
    }
}

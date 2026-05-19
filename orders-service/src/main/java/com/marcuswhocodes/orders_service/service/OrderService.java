package com.marcuswhocodes.orders_service.service;

import com.marcuswhocodes.kafka.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {


    @KafkaListener(topics = "orders", groupId = "order-service-group")
    public void createOrder(OrderEvent orderEvent){
        log.info("Creating Order: {}", orderEvent);
    }
}

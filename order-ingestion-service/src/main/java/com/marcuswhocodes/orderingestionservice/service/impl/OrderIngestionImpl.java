package com.marcuswhocodes.orderingestionservice.service.impl;

import com.marcuswhocodes.orderingestionservice.dto.OrderIngestionDto;
import com.marcuswhocodes.orderingestionservice.service.OrderIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderIngestionImpl implements OrderIngestionService {
    private final KafkaTemplate<String, OrderIngestionDto> kafkaTemplate;

    @Override
    public void ingestOrder(OrderIngestionDto orderIngestionDto) {
        OrderIngestionDto event = OrderIngestionDto.builder()
                .userId(orderIngestionDto.userId())
                .cartId(orderIngestionDto.cartId())
                .timestamp(orderIngestionDto.timestamp())
                .build();
        kafkaTemplate.send("orders", event);
        log.info("order sent to Kafka: {}", event);
    }
}

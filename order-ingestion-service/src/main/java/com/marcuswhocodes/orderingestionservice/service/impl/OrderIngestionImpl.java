package com.marcuswhocodes.orderingestionservice.service.impl;

import com.marcuswhocodes.kafka.event.OrderEvent;
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
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Override
    public void ingestOrder(OrderIngestionDto orderIngestionDto) {
        OrderEvent event = OrderEvent.builder()
                .userId(orderIngestionDto.userId())
                .eventId(orderIngestionDto.eventId())
                .timestamp(orderIngestionDto.timestamp())
                .build();
        kafkaTemplate.send("orders", event);
        log.info("order sent to Kafka: {}", event);
    }
}

package com.marcuswhocodes.orderingestionservice.simullation;

import com.marcuswhocodes.orderingestionservice.dto.OrderIngestionDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class ParallelOrderSimulation implements CommandLineRunner {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${simulation.parallel-threads}")
    private int parallelThreads;


    @Value("${simulation.requests-per-interval}")
    private int requestsPerInterval;

    @Value("${simulation.ingestion-endpoint}")
    private String ingestionEndpoint;

    private final ExecutorService executorService;

    public ParallelOrderSimulation() {
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("ParallelDataSimulator started");
        ((ThreadPoolExecutor)executorService).setCorePoolSize(parallelThreads);
    }

    @Scheduled(fixedRateString = "${simulation.interval-ms}")
    public void sendMockData(){
        int batchSize = requestsPerInterval / parallelThreads;
        int remainder = requestsPerInterval % parallelThreads;

        for (int i = 0; i < parallelThreads; i++) {
            int requestForThread = batchSize + (i < remainder ? 1 : 0);
            executorService.submit(() -> {
                for (int j = 0; j < requestForThread; j++) {
                    OrderIngestionDto dto = OrderIngestionDto.builder()
                            .userId(UUID.fromString("c245e42d-c992-408a-9740-949597e78005"))
                            .cartId(UUID.fromString("c245e42d-c992-408a-9740-949597e78005"))
                            .timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                            .build();
                    try{
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<OrderIngestionDto> request = new HttpEntity<>(dto, headers);
                        restTemplate.postForEntity(ingestionEndpoint, request, Void.class);
                        log.info("Send mock data: {}", dto);
                    } catch (Exception e) {
                        log.error("failed to send data: {}", e.getMessage());
                    }
                }
            });
        }
    }

    @PreDestroy
    public void shutdown(){
        executorService.shutdown();
        log.info("ParallelDataSimulator stopped");
    }

}

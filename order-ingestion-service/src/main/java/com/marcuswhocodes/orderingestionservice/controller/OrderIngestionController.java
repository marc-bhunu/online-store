package com.marcuswhocodes.orderingestionservice.controller;


import com.marcuswhocodes.orderingestionservice.dto.OrderIngestionDto;
import com.marcuswhocodes.orderingestionservice.service.OrderIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ingestion")
public class OrderIngestionController {

    private final OrderIngestionService orderIngestionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void ingestData(@RequestBody OrderIngestionDto orderIngestionDto){
        orderIngestionService.ingestOrder(orderIngestionDto);
    }
}

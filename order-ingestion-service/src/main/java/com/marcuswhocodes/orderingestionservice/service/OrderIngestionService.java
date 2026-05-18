package com.marcuswhocodes.orderingestionservice.service;

import com.marcuswhocodes.orderingestionservice.dto.OrderIngestionDto;

public interface OrderIngestionService {
    void ingestOrder(OrderIngestionDto orderIngestionDto);
}

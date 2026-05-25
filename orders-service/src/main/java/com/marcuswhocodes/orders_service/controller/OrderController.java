package com.marcuswhocodes.orders_service.controller;

import com.marcuswhocodes.orders_service.domain.dtos.order.OrderDto;
import com.marcuswhocodes.orders_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @GetMapping("/all")
    private ResponseEntity<List<OrderDto>> getAllOrders(){
        List<OrderDto> response = orderService.getAllOrders();
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }
}

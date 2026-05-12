package com.marcusehocodes.cart_service.controller;

import com.marcusehocodes.cart_service.domain.dto.CartDto;
import com.marcusehocodes.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    private ResponseEntity<CartDto> createCart(@RequestBody CartDto cartDto) {
        CartDto response = cartService.createCart(cartDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    private ResponseEntity<CartDto> getCartByUserId(@PathVariable UUID userId) {
        CartDto response = cartService.getCartByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

package com.marcusehocodes.cart_service.controller;

import com.marcusehocodes.cart_service.domain.dto.CartResponseDto;
import com.marcusehocodes.cart_service.domain.dto.CreateCartRequestDto;
import com.marcusehocodes.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    private ResponseEntity<CartResponseDto> createCart(@RequestBody CreateCartRequestDto cartDto) {
        CartResponseDto response = cartService.createCart(cartDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    private ResponseEntity<List<CartResponseDto>> getAllCarts(){
        List<CartResponseDto> response = cartService.getAllCarts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    private ResponseEntity<CartResponseDto> getCartByUserId(@PathVariable UUID userId) {
        CartResponseDto response = cartService.getCartByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    private ResponseEntity<Void> deleteCartByUserId(@PathVariable UUID userId) {
        cartService.deleteCartByUserId(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.marcusehocodes.cart_service.service;

import com.marcusehocodes.cart_service.domain.dto.CartDto;

import java.util.UUID;

public interface CartService {
    CartDto createCart(CartDto cartDto);

    CartDto getCartByUserId(UUID userId);
}

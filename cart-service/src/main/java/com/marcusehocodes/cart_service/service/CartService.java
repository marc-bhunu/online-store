package com.marcusehocodes.cart_service.service;

import com.marcusehocodes.cart_service.domain.dto.CartResponseDto;
import com.marcusehocodes.cart_service.domain.dto.CreateCartRequestDto;

import java.util.UUID;

public interface CartService {
    CartResponseDto createCart(CreateCartRequestDto cartDto);
    CartResponseDto getCartByUserId(UUID userId);
    void deleteCartByUserId(UUID userId);
}

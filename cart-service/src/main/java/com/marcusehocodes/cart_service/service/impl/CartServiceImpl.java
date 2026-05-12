package com.marcusehocodes.cart_service.service.impl;

import com.marcusehocodes.cart_service.client.UserClient;
import com.marcusehocodes.cart_service.domain.documents.Cart;
import com.marcusehocodes.cart_service.domain.documents.LineItem;
import com.marcusehocodes.cart_service.domain.dto.CartDto;
import com.marcusehocodes.cart_service.domain.dto.UserDto;
import com.marcusehocodes.cart_service.repository.CartRepository;
import com.marcusehocodes.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserClient userClient;

    @Override
    public CartDto createCart(CartDto cartDto) {
        UserDto user = userClient.getUserById(cartDto.getUserId());
        if (user == null) throw new IllegalArgumentException("User with id " + cartDto.getUserId() + " does not exist");

        List<LineItem> lineItems = cartDto.getLineItems()
                .stream()
                .map(item -> LineItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        Long totalPrice = cartDto.getLineItems()
                .stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        Cart cart = Cart.builder()
                .userId(cartDto.getUserId())
                .lineItems(lineItems)
                .totalPrice(totalPrice)
                .currency(cartDto.getCurrency())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Cart response = cartRepository.insert(cart);
        return mapToDto(response);
    }

    @Override
    public CartDto getCartByUserId(UUID userId) {
        List<Cart> carts = cartRepository.findCartByUserId(userId);
        if (carts.isEmpty()) {
            throw new IllegalArgumentException("Cart for user with id " + userId + " does not exist");
        }
        return mapToDto(carts.get(0));
    }

    private CartDto mapToDto(Cart response) {
        return CartDto.builder()
                .userId(response.getUserId())
                .lineItems(new ArrayList<>())
                .totalPrice(response.getTotalPrice())
                .currency(response.getCurrency())
                .build();
    }
}

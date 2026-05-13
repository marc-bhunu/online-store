package com.marcusehocodes.cart_service.service.impl;

import com.marcusehocodes.cart_service.client.UserClient;
import com.marcusehocodes.cart_service.domain.documents.Cart;
import com.marcusehocodes.cart_service.domain.documents.LineItem;
import com.marcusehocodes.cart_service.domain.dto.CartResponseDto;
import com.marcusehocodes.cart_service.domain.dto.CreateCartRequestDto;
import com.marcusehocodes.cart_service.domain.dto.LineItemDto;
import com.marcusehocodes.cart_service.domain.dto.UserDto;
import com.marcusehocodes.cart_service.repository.CartRepository;
import com.marcusehocodes.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserClient userClient;

    @Override
    public CartResponseDto createCart(CreateCartRequestDto cartDto) {
        UserDto user = userClient.getUserById(cartDto.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User with id " + cartDto.getUserId() + " does not exist");
        }

        if (cartRepository.existsCartByUserId(cartDto.getUserId())) {
            Cart cart = updateCart(cartRepository.findCartByUserId(cartDto.getUserId()).get(0), cartDto);
            return mapToDto(cartRepository.save(cart));
        }

        List<LineItem> lineItems = cartDto.getLineItems()
                .stream()
                .map(item -> LineItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        Long totalPrice = cartDto
                .getLineItems()
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
    public CartResponseDto getCartByUserId(UUID userId) {
        List<Cart> carts = cartRepository.findCartByUserId(userId);
        if (carts.isEmpty()) {
            throw new IllegalArgumentException("Cart for user with id " + userId + " does not exist");
        }
        return mapToDto(carts.get(0));
    }

    @Override
    public void deleteCartByUserId(UUID userId) {
        List<Cart> carts = cartRepository.findCartByUserId(userId);
        if (carts.isEmpty()) {
            throw new IllegalArgumentException("Cart for user with id " + userId + " does not exist");
        }
        cartRepository.delete(carts.get(0));
    }

    private CartResponseDto mapToDto(Cart response) {
        List<LineItemDto> lineItems = response
                .getLineItems()
                .stream()
                .map(item -> LineItemDto.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build()).collect(Collectors.toList());
        return CartResponseDto.builder()
                .userId(response.getUserId())
                .lineItems(lineItems)
                .totalPrice(response.getTotalPrice())
                .currency(response.getCurrency())
                .build();
    }

    private Cart updateCart(Cart cart, CreateCartRequestDto cartDto) {
        List<LineItem> existingLineItems = cart.getLineItems();
        Long priceToAdd = 0L;
        for (LineItemDto incomingItem : cartDto.getLineItems()) {
            boolean productExists = false;
            for (LineItem existingItem : existingLineItems) {
                if (existingItem.getProductId().equals(incomingItem.getProductId())) {
                    existingItem.setQuantity(existingItem.getQuantity() + incomingItem.getQuantity());
                    priceToAdd += (long) incomingItem.getPrice() * incomingItem.getQuantity();
                    productExists = true;
                    break;
                }
            }
            if (!productExists) {
                LineItem newItem = LineItem.builder()
                        .productId(incomingItem.getProductId())
                        .productName(incomingItem.getProductName())
                        .price(incomingItem.getPrice())
                        .quantity(incomingItem.getQuantity())
                        .build();
                existingLineItems.add(newItem);
                priceToAdd += (long) incomingItem.getPrice() * incomingItem.getQuantity();
            }
        }

        cart.setLineItems(existingLineItems);
        cart.setTotalPrice(cart.getTotalPrice() + priceToAdd);
        cart.setCurrency(cartDto.getCurrency());
        cart.setUpdatedAt(LocalDateTime.now());
        return cart;
    }
}

package com.marcuswhocodes.orders_service.clients;

import com.marcuswhocodes.orders_service.domain.dtos.cart.CartResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Configuration
public class CartClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CartClient(@Value("${cart.service.url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }


    public CartResponseDto getCartByUserId(UUID userId) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUriString();
        ResponseEntity<CartResponseDto> response = restTemplate.getForEntity(url, CartResponseDto.class);
        return response.getBody();
    }


}

package com.marcuswhocodes.orders_service.clients;

import com.marcuswhocodes.orders_service.domain.dtos.product.ReverseProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Configuration
public class ProductClient {

    private final String baseUrl;
    private  final RestTemplate restTemplate;

    public ProductClient(@Value("${product.service.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public void reserveProduct(List<ReverseProductDto> products) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/reserve")
                .build()
                .toUriString();
        restTemplate.postForEntity(uri, new HttpEntity<>(products), Void.class);
    }

}

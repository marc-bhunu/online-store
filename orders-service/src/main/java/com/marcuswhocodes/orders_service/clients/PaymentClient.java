package com.marcuswhocodes.orders_service.clients;

import com.marcuswhocodes.orders_service.domain.dtos.payment.PaymentRequest;
import com.marcuswhocodes.orders_service.domain.payment.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class PaymentClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public PaymentClient(@Value("${services.payment-service.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public StripeResponse processPayment(PaymentRequest paymentRequest) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/checkout")
                .build()
                .toUriString();
        ResponseEntity<StripeResponse> response = restTemplate.postForEntity(url, paymentRequest, StripeResponse.class);
        return response.getBody();
    }
}

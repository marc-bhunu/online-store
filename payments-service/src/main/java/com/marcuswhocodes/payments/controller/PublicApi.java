package com.marcuswhocodes.payments.controller;

import com.marcuswhocodes.payments.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@Slf4j
public class PublicApi {

    private final StripeService stripeService;
    private final String endpointSecret;

    public PublicApi(StripeService stripeService, @Value("${stripe.webhook.secret}") String endpointSecret) {
        this.stripeService = stripeService;
        this.endpointSecret = endpointSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            stripeService.capturePayment(event);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature: {}", e.getMessage());
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}

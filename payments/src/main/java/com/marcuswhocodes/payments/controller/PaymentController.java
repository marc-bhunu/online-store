package com.marcuswhocodes.payments.controller;

import com.marcuswhocodes.payments.domain.dto.ProductRequest;
import com.marcuswhocodes.payments.domain.dto.StripeResponse;
import com.marcuswhocodes.payments.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class PaymentController {
    private final StripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest){
        var response = stripeService.checkoutProducts(productRequest);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

}

package com.marcuswhocodes.payments.service;

import com.marcuswhocodes.payments.domain.ProductRequest;
import com.marcuswhocodes.payments.domain.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productRequest.getName())
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(productRequest.getCurrency() == null ? "USD" : productRequest.getCurrency())
                .setUnitAmount(productRequest.getAmount() * 100)
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(productRequest.getQuantity())
                .setPriceData(priceData)
                .build();

        var params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment Session Created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    public void capturePayment(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            try {
                stripeObject = dataObjectDeserializer.deserializeUnsafe();
            } catch (com.stripe.exception.EventDataObjectDeserializationException e) {
                System.err.println("Fatal: Unable to deserialize even unsafely. " + e.getMessage());
                return;
            }
        }
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("Payment succeeded for ID: " + paymentIntent.getId());
                System.out.println("Amount paid: " + paymentIntent.getAmount());
                break;

            case "charge.refunded":
                Charge charge = (Charge) stripeObject;
                System.out.println("Refund processed for Charge ID: " + charge.getId());
                break;
            case "checkout.session.completed":
                Session session = (Session) stripeObject;
                System.out.println("Session completed for ID: " + session.getId());
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
    }
}

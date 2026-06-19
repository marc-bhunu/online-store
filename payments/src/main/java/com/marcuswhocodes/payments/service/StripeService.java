package com.marcuswhocodes.payments.service;

import com.marcuswhocodes.payments.domain.dto.PaymentItems;
import com.marcuswhocodes.payments.domain.dto.PaymentRequest;
import com.marcuswhocodes.payments.domain.dto.StripeResponse;
import com.marcuswhocodes.payments.domain.entity.Payment;
import com.marcuswhocodes.payments.domain.entity.PaymentItem;
import com.marcuswhocodes.payments.domain.enums.PaymentMethod;
import com.marcuswhocodes.payments.domain.enums.PaymentStatus;
import com.marcuswhocodes.payments.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public StripeResponse checkoutProducts(PaymentRequest paymentRequest) {
        Stripe.apiKey = secretKey;
        StripeResponse response = null;
        Session session = null;
        SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel");

        if(paymentRepository.existsByIdempotencyKey(paymentRequest.getIdempotencyKey())){;
            log.info("Payment with idempotency key {} already exists. Returning existing session.", paymentRequest.getIdempotencyKey());
            return StripeResponse.builder()
                    .status("error")
                    .message("Duplicate payment request")
                    .build();
        }
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .userId(paymentRequest.getUserId())
                .idempotencyKey(paymentRequest.getIdempotencyKey())
                .totalAmount(paymentRequest.getTotalAmount())
                .currency(paymentRequest.getCurrency())
                .paymentItems(new ArrayList<>())
                .status(PaymentStatus.INITIATED)
                .paymentMethod(paymentRequest.getPaymentMethod())
                .build();
        List<PaymentItem> paymentItems = paymentRequest.getPaymentItems().stream()
                .map(item -> PaymentItem.builder()
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .amount(item.getAmount())
                        .currency(item.getCurrency())
                        .payment(payment)
                        .build())
                .toList();
        payment.setPaymentItems(paymentItems);
        for (PaymentItems item : paymentRequest.getPaymentItems()) {
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(item.getName())
                    .build();
            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(paymentRequest.getCurrency() == null ? "USD" : paymentRequest.getCurrency())
                    .setUnitAmount(item.getAmount() * 100)
                    .setProductData(productData)
                    .build();
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(item.getQuantity())
                    .setPriceData(priceData)
                    .build();
            sessionBuilder.addLineItem(lineItem);
        }
        var params = sessionBuilder.build();
        try {
            session = Session.create(params);
            response = StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Payment Session Created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();
            payment.setSessionId(session.getId());
            paymentRepository.save(payment);
        } catch (StripeException e) {
            response = StripeResponse.builder()
                    .status("FAILED")
                    .message(e.getMessage())
                    .sessionUrl(null)
                    .sessionId(null)
                    .build();
        }
        return response;
    }

    public void capturePayment(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            try {
                stripeObject = dataObjectDeserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
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
                Payment payment = paymentRepository.findBySessionId(session.getId());
                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                kafkaTemplate.send("payment-events", "Payment completed for session ID: " + session.getId());
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
    }
}

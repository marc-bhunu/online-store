package com.marcuswhocodes.orders_service.service;

import com.marcuswhocodes.kafka.event.OrderEvent;
import com.marcuswhocodes.kafka.event.PaymentRequestEvent;
import com.marcuswhocodes.orders_service.clients.CartClient;
import com.marcuswhocodes.orders_service.clients.UserClient;
import com.marcuswhocodes.orders_service.domain.dtos.cart.CartResponseDto;
import com.marcuswhocodes.orders_service.domain.dtos.order.OrderAddressDto;
import com.marcuswhocodes.orders_service.domain.dtos.order.OrderDto;
import com.marcuswhocodes.orders_service.domain.dtos.order.OrderItemDto;
import com.marcuswhocodes.orders_service.domain.dtos.user.UserDto;
import com.marcuswhocodes.orders_service.domain.entity.Order;
import com.marcuswhocodes.orders_service.domain.entity.OrderAddress;
import com.marcuswhocodes.orders_service.domain.entity.OrderItem;
import com.marcuswhocodes.orders_service.domain.enums.AddressType;
import com.marcuswhocodes.orders_service.domain.enums.OrderStatus;
import com.marcuswhocodes.orders_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final CartClient cartClient;
    private final UserClient userClient;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, PaymentRequestEvent> paymentRequestEventKafkaTemplate;

    @KafkaListener(topics = "orders", groupId = "order-service-group")
    public void createOrder(OrderEvent orderEvent){
        CartResponseDto cartResponse = cartClient.getCartByUserId(orderEvent.userId());
        UserDto userResponse = userClient.getUserById(orderEvent.userId());
        if (userResponse == null || cartResponse == null) {
            log.warn("Failed to create order for user: {}", userResponse != null ? userResponse.getEmail() : "Unknown");
        } else {
            Order order = createOrder(userResponse, cartResponse);
            PaymentRequestEvent payment = PaymentRequestEvent.builder()
                    .userId(order.getUserId())
                    .idempotencyKey((order.getId().toString() + order.getUserId().toString()))
                    .orderId(order.getId())
                    .paymentMethod("CARD")
                    .amount(order.getTotalAmount())
                    .currency(order.getCurrency())
                    .timestamp(Instant.now())
                    .build();
            paymentRequestEventKafkaTemplate.send("payment-requests", payment);
            log.info("Order created successfully for user: {}", userResponse.getEmail());
        }
    }

    public Order createOrder(UserDto user, CartResponseDto cart) {
        Order order = Order.builder()
                .userId(cart.getUserId())
                .status(OrderStatus.CREATED)
                .orderItems(new ArrayList<>())
                .orderAddress(new OrderAddress())
                .totalAmount(cart.getTotalPrice())
                .currency(cart.getCurrency())
                .build();

        OrderAddress orderAddress = user.getAddresses()
                .stream()
                .filter(addressDto -> addressDto.getType() == AddressType.BILLING)
                .map(addressDto -> OrderAddress.builder()
                        .type(AddressType.BILLING)
                        .order(order)
                        .line1(addressDto.getLine1())
                        .line2(addressDto.getLine2())
                        .city(addressDto.getCity())
                        .state(addressDto.getState())
                        .zip(addressDto.getZip())
                        .country(addressDto.getCountry()).build())
                .findFirst().orElse(null);

        List<OrderItem> orderItems = cart
                .getLineItems()
                .stream()
                .map(item -> OrderItem.builder()
                        .order(order)
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getPrice() * item.getQuantity())
                        .build())
                .toList();
        order.setOrderItems(orderItems);
        order.setOrderAddress(orderAddress);
        return orderRepository.save(order);
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return mapToDto(orders);
    }

    private List<OrderDto> mapToDto(List<Order> orders) {
        return orders.stream().map(order -> OrderDto.builder()
                        .userId(order.getUserId())
                        .status(order.getStatus())
                        .orderItems(order.getOrderItems()
                                .stream().map(item -> OrderItemDto.builder()
                                        .productId(item.getProductId())
                                        .productName(item.getProductName())
                                        .price(item.getPrice())
                                        .quantity(item.getQuantity())
                                        .subtotal(item.getSubtotal())
                                        .build()).collect(Collectors.toList()))
                        .orderAddress(OrderAddressDto.builder()
                                .type(order.getOrderAddress().getType())
                                .line1(order.getOrderAddress().getLine1())
                                .line2(order.getOrderAddress().getLine2())
                                .city(order.getOrderAddress().getCity())
                                .state(order.getOrderAddress().getState())
                                .zip(order.getOrderAddress().getZip())
                                .country(order.getOrderAddress().getCountry())
                                .build())
                        .totalAmount(order.getTotalAmount())
                        .currency(order.getCurrency())
                        .build()).collect(Collectors.toList());
    }
}

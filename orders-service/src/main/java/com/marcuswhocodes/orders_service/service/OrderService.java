package com.marcuswhocodes.orders_service.service;

import com.marcuswhocodes.kafka.event.OrderEvent;
import com.marcuswhocodes.orders_service.clients.CartClient;
import com.marcuswhocodes.orders_service.clients.UserClient;
import com.marcuswhocodes.orders_service.domain.dtos.cart.CartResponseDto;
import com.marcuswhocodes.orders_service.domain.dtos.user.UserDto;
import com.marcuswhocodes.orders_service.domain.entity.AddressSnapshot;
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

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final CartClient cartClient;
    private final UserClient userClient;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate;
    @KafkaListener(topics = "orders", groupId = "order-service-group")
    public void createOrder(OrderEvent orderEvent){
        CartResponseDto cartResponse = cartClient.getCartByUserId(orderEvent.userId());
        UserDto userResponse = userClient.getUserById(orderEvent.userId());
        log.info("Creating Order: {}", orderEvent);
        log.info("Cart Response: {}", cartResponse);
        log.info("User Response: {}", userResponse);

        if (userResponse == null || cartResponse == null) {
            log.warn("Failed to create order for user: {}", userResponse != null ? userResponse.getEmail() : "Unknown");
        } else {
            createOrder(userResponse, cartResponse);
            //orderEventKafkaTemplate.send("order-created", orderEvent);
            log.info("Order created successfully for user: {}", userResponse.getEmail());
        }
    }

    public void createOrder(UserDto user, CartResponseDto cart) {
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
                        .addressSnapshot(AddressSnapshot.builder()
                                .type(AddressType.BILLING)
                                .line1(addressDto.getLine1())
                                .line2(addressDto.getLine2())
                                .city(addressDto.getCity())
                                .state(addressDto.getState())
                                .zip(addressDto.getZip())
                                .country(addressDto.getCountry())
                                .city(addressDto.getCity())
                                .build())
                        .build()).findFirst().orElse(null);

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
        orderRepository.save(order);
    }
}

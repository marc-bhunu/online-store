package com.marcuswhocodes.orders_service.service;

import com.marcuswhocodes.kafka.event.NotificationEvent;
import com.marcuswhocodes.kafka.event.OrderEvent;
import com.marcuswhocodes.orders_service.clients.CartClient;
import com.marcuswhocodes.orders_service.clients.PaymentClient;
import com.marcuswhocodes.orders_service.clients.ProductClient;
import com.marcuswhocodes.orders_service.clients.UserClient;
import com.marcuswhocodes.orders_service.domain.dtos.order.CreateOrderDto;
import com.marcuswhocodes.orders_service.domain.dtos.order.OrderAddressDto;
import com.marcuswhocodes.orders_service.domain.dtos.order.OrderDto;
import com.marcuswhocodes.orders_service.domain.dtos.order.OrderItemDto;
import com.marcuswhocodes.orders_service.domain.dtos.payment.PaymentItems;
import com.marcuswhocodes.orders_service.domain.dtos.payment.PaymentRequest;
import com.marcuswhocodes.orders_service.domain.dtos.product.ReverseProductDto;
import com.marcuswhocodes.orders_service.domain.dtos.user.UserDto;
import com.marcuswhocodes.orders_service.domain.entity.Order;
import com.marcuswhocodes.orders_service.domain.entity.OrderAddress;
import com.marcuswhocodes.orders_service.domain.entity.OrderItem;
import com.marcuswhocodes.orders_service.domain.enums.AddressType;
import com.marcuswhocodes.orders_service.domain.enums.OrderStatus;
import com.marcuswhocodes.orders_service.domain.enums.PaymentMethod;
import com.marcuswhocodes.orders_service.domain.enums.PaymentStatus;
import com.marcuswhocodes.orders_service.domain.payment.StripeResponse;
import com.marcuswhocodes.orders_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final CartClient cartClient;
    private final UserClient userClient;
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public StripeResponse createOrder(CreateOrderDto createOrderDto) {
        Order order = create(createOrderDto);

        List<ReverseProductDto> reverseProducts = createOrderDto.getOrderItems().stream()
                .map(item -> new ReverseProductDto(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());
        productClient.reserveProduct(reverseProducts);

        List<PaymentItems> paymentItems = order.getOrderItems().stream().map(item -> PaymentItems.builder()
                        .amount(item.getPrice())
                        .quantity(Long.valueOf(item.getQuantity()))
                        .name(item.getProductName())
                        .currency(order.getCurrency())
                        .build())
                .collect(Collectors.toList());

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .idempotencyKey(order.getId().toString() + order.getUserId().toString())
                .sessionId(null)
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .status(PaymentStatus.INITIATED)
                .paymentItems(paymentItems)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        //delete the users cart after the order has been created
        //send an order confirmation email to the user
        return paymentClient.processPayment(paymentRequest);

    }

    private Order create(CreateOrderDto createOrderDto) {
        Long totalAmount = createOrderDto.getOrderItems().stream().map(item -> item.getPrice() * item.getQuantity()).reduce(0L, Long::sum);

        Order order = Order.builder()
                .userId(createOrderDto.getUserId())
                .status(OrderStatus.CREATED)
                .orderItems(new ArrayList<>())
                .orderAddress(new OrderAddress())
                .totalAmount(totalAmount)
                .currency(createOrderDto.getCurrency())
                .build();
        UserDto user = userClient.getUserById(createOrderDto.getUserId());

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

        List<OrderItem> orderItems = createOrderDto
                .getOrderItems()
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


    @KafkaListener(topics = "order-completed", groupId = "order-service-group")
    public void updateOrder(OrderEvent orderEvent) {
        log.info("order completed event received: {}", orderEvent);
        Order order = orderRepository.findById(orderEvent.orderId()).orElse(null);
        if (order == null) {
            return;
        }
        order.setStatus(OrderStatus.CONFIRMED);
        Order created = orderRepository.save(order);
        if (created != null) {
            kafkaTemplate.send("notifications", NotificationEvent.builder()
                    .orderId(created.getId())
                    .email("macbhunu@gmail.com")
                    .status(created.getStatus())
                    .build());
        }
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

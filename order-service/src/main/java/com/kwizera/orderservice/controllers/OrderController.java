package com.kwizera.orderservice.controllers;

import com.kwizera.orderservice.domain.dtos.CreateOrderDTO;
import com.kwizera.orderservice.domain.dtos.OrderDTO;
import com.kwizera.orderservice.domain.dtos.UpdateOrderStatusDTO;
import com.kwizera.orderservice.domain.entities.Order;
import com.kwizera.orderservice.domain.events.OrderPlacedEvent;
import com.kwizera.orderservice.domain.events.OrderUpdatedEvent;
import com.kwizera.orderservice.service.eventServices.OrderEventPublisher;
import com.kwizera.orderservice.service.OrderServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderServices orderServices;
    private final OrderEventPublisher orderEventPublisher;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        List<OrderDTO> orders = orderServices.getUserOrders(Long.parseLong(userId), authHeader);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody CreateOrderDTO orderDetails,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long clientId = Long.parseLong(userId);
        OrderDTO order = orderServices.createOrder(orderDetails, clientId, authHeader);

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .id(order.id())
                .client(order.customer())
                .restaurant(order.restaurant())
                .timestamp(LocalDateTime.now())
                .build();

        orderEventPublisher.publishOrderPlaced(event);

        return new ResponseEntity<>(
                order,
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateOrderStatusDTO newOrderStatus,
            @PathVariable Long orderId) {

        if (!"OWNER".equals(role)) {
            return new ResponseEntity<>(
                    "Only owners can update orders",
                    HttpStatus.UNAUTHORIZED
            );
        }

        Order updatedOrder = orderServices.updateOrderStatus(orderId, newOrderStatus);
        if (updatedOrder == null) {
            return new ResponseEntity<>(
                    "Order status could not be updated",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        OrderUpdatedEvent event = OrderUpdatedEvent.builder()
                .id(updatedOrder.getId())
                .newStatus(updatedOrder.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        orderEventPublisher.publishOrderUpdated(event);

        return new ResponseEntity<>(
                "Order status updated to " + updatedOrder.getStatus(),
                HttpStatus.OK
        );
    }
}

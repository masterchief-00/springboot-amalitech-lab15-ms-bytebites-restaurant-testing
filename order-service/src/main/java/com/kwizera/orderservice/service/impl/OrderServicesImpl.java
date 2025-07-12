package com.kwizera.orderservice.service.impl;

import com.kwizera.orderservice.domain.dtos.*;
import com.kwizera.orderservice.domain.entities.Order;
import com.kwizera.orderservice.domain.entities.OrderItem;
import com.kwizera.orderservice.domain.enums.OrderStatus;
import com.kwizera.orderservice.repositories.OrderRepository;
import com.kwizera.orderservice.service.OrderServices;
import com.kwizera.orderservice.service.RemoteServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderServicesImpl implements OrderServices {
    private final OrderRepository orderRepository;
    private final RemoteServiceClient remoteServiceClient;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderDTO orderDetails, Long clientId, String token) {
        Order order = Order.builder()
                .clientId(clientId)
                .restaurantId(orderDetails.restaurantId())
                .build();

        List<OrderItem> items = orderDetails.items().stream().map(
                item -> {
                    return OrderItem.builder()
                            .foodId(item.foodId())
                            .quantity(item.quantity())
                            .build();
                }
        ).toList();

        order.setItems(items);

        for (OrderItem item : items) {
            item.setOrder(order);
        }

        Order createdOrder = orderRepository.save(order);

        // fetch more details for the order created
        UserDTO client = remoteServiceClient.fetchClient(clientId, token);
        RestaurantDTO restaurant = remoteServiceClient.fetchRestaurant(orderDetails.restaurantId(), token);

        List<OrderItemDTO> enrichedItems = items.stream().map(
                orderItem -> {
                    FoodDTO food = remoteServiceClient.fetchFood(orderItem.getFoodId(), token);

                    return OrderItemDTO.builder()
                            .name(food.name())
                            .quantity(orderItem.getQuantity())
                            .subTotal(food.price() * orderItem.getQuantity())
                            .build();
                }
        ).toList();

        Double price = enrichedItems.stream().mapToDouble(OrderItemDTO::subTotal).sum();

        return OrderDTO.builder()
                .id(order.getId())
                .customer(client.names())
                .restaurant(restaurant.name())
                .totalPrice(price)
                .items(enrichedItems)
                .build();
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId, String token) {
        List<Order> fetchedOrders = orderRepository.findAllByClientId(userId);

        // fetch more details for the orders
        UserDTO client = remoteServiceClient.fetchClient(userId, token);
        return fetchedOrders.stream().map(
                order -> {
                    RestaurantDTO restaurant = remoteServiceClient.fetchRestaurant(order.getRestaurantId(), token);

                    List<OrderItemDTO> orderItemDTOS = order.getItems().stream().map(
                            orderItem -> {
                                FoodDTO food = remoteServiceClient.fetchFood(orderItem.getFoodId(), token);

                                return OrderItemDTO.builder()
                                        .name(food.name())
                                        .quantity(orderItem.getQuantity())
                                        .subTotal(food.price() * orderItem.getQuantity())
                                        .build();
                            }
                    ).toList();

                    Double price = orderItemDTOS.stream().mapToDouble(OrderItemDTO::subTotal).sum();

                    return OrderDTO.builder()
                            .id(order.getId())
                            .customer(client.names())
                            .restaurant(restaurant.name())
                            .totalPrice(price)
                            .items(orderItemDTOS)
                            .build();
                }
        ).toList();
    }

    @Override
    public Order updateOrderStatus(Long orderId, UpdateOrderStatusDTO newOrderStatus) {
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        OrderStatus status = null;

        switch (newOrderStatus.newStatus()) {
            case 1 -> status = OrderStatus.PENDING;
            case 2 -> status = OrderStatus.CONFIRMED;
            case 3 -> status = OrderStatus.DELIVERED;
            case 4 -> status = OrderStatus.CANCELLED;
            default -> {
                return null;
            }
        }

        if (foundOrder.isPresent()) {
            Order order = foundOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
            return order;
        }

        return null;
    }
}

package com.kwizera.orderservice.service;

import com.kwizera.orderservice.domain.dtos.CreateOrderDTO;
import com.kwizera.orderservice.domain.dtos.OrderDTO;
import com.kwizera.orderservice.domain.dtos.UpdateOrderStatusDTO;
import com.kwizera.orderservice.domain.entities.Order;

import java.util.List;

public interface OrderServices {
    OrderDTO createOrder(CreateOrderDTO orderDetails, Long clientId, String token);

    List<OrderDTO> getUserOrders(Long userId, String token);

    Order updateOrderStatus(Long orderId, UpdateOrderStatusDTO orderStatus);
}

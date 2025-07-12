package com.kwizera.orderservice.service.eventServices;

import com.kwizera.orderservice.domain.events.OrderPlacedEvent;
import com.kwizera.orderservice.domain.events.OrderUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishOrderPlaced(OrderPlacedEvent event) {
        rabbitTemplate.convertAndSend("order.exchange", "order.placed", event);
    }

    public void publishOrderUpdated(OrderUpdatedEvent event) {
        rabbitTemplate.convertAndSend("order.exchange", "order.updated", event);
    }
}

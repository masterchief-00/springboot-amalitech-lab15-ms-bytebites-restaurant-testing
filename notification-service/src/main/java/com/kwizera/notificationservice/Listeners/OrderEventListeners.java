package com.kwizera.notificationservice.Listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.kwizera.notificationservice.domain.events.OrderPlacedEvent;
import com.kwizera.notificationservice.domain.events.OrderUpdatedEvent;
import com.kwizera.notificationservice.utils.CustomLogger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class OrderEventListeners {
    private static final Path outputFile = Paths.get(System.getProperty("user.home"), "byteBites_notifications.json");
    private final ObjectMapper mapper = new ObjectMapper();

    @RabbitListener(queues = "order.placed.queue")
    public void handlePlaceOrderEvent(OrderPlacedEvent event) {
        try {
            File notificationFile = outputFile.toFile();
            ObjectNode notification = mapper.createObjectNode();
            notification.put("orderId", String.valueOf(event.getId()));
            notification.put("clientName", String.valueOf(event.getClient()));
            notification.put("restaurant", String.valueOf(event.getRestaurant()));
            notification.put("timestamp", String.valueOf(event.getTimestamp()));

            ArrayNode oldNotifications;
            if (notificationFile.exists()) {
                oldNotifications = (ArrayNode) mapper.readTree(notificationFile);
            } else {
                oldNotifications = mapper.createArrayNode();
            }
            oldNotifications.add(notification);
            mapper.writerWithDefaultPrettyPrinter().writeValue(notificationFile, oldNotifications);
            CustomLogger.log(CustomLogger.LogLevel.INFO, "NEW ORDER: A new order placed for " + event.getRestaurant());
        } catch (Exception e) {
            CustomLogger.log(CustomLogger.LogLevel.ERROR, e.getMessage());
            CustomLogger.log(CustomLogger.LogLevel.INFO, "NEW ORDER: A new order placed for " + event.getRestaurant());
            throw new RuntimeException(e.getMessage());
        }

    }

    @RabbitListener(queues = "order.updated.queue")
    public void handleUpdatedOrderEvent(OrderUpdatedEvent event) {
        try {
            File notificationFile = outputFile.toFile();
            ObjectNode notification = mapper.createObjectNode();
            notification.put("orderId", String.valueOf(event.getId()));
            notification.put("newStatus", String.valueOf(event.getNewStatus()));
            notification.put("timestamp", String.valueOf(event.getTimestamp()));

            ArrayNode oldNotifications;
            if (notificationFile.exists()) {
                oldNotifications = (ArrayNode) mapper.readTree(notificationFile);
            } else {
                oldNotifications = mapper.createArrayNode();
            }
            oldNotifications.add(notification);
            mapper.writerWithDefaultPrettyPrinter().writeValue(notificationFile, oldNotifications);
            CustomLogger.log(CustomLogger.LogLevel.INFO, "UPDATED ORDER: order #" + event.getId() + " status changed to " + event.getNewStatus());
        } catch (Exception e) {
            CustomLogger.log(CustomLogger.LogLevel.ERROR, e.getMessage());
            CustomLogger.log(CustomLogger.LogLevel.INFO, "UPDATED ORDER: order #" + event.getId() + " status changed to " + event.getNewStatus());
            throw new RuntimeException(e.getMessage());
        }

    }
}

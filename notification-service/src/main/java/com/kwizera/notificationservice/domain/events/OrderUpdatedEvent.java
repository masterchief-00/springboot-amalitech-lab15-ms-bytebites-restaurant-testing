package com.kwizera.notificationservice.domain.events;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.kwizera.notificationservice.domain.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderUpdatedEvent {
    private Long id;

    private OrderStatus newStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}

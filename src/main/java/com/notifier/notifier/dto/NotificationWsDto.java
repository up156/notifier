package com.notifier.notifier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Дто для нотификаций websocket")
public record NotificationWsDto(

        Long eventId,
        String message,
        String sentTo,
        LocalDateTime sentAt
) {
}

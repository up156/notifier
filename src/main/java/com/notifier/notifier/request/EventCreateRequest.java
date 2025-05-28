package com.notifier.notifier.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Запрос на создание события")
public record EventCreateRequest(

        @NotBlank
        String message,
        @FutureOrPresent
        LocalDateTime eventDateTime
) {
}

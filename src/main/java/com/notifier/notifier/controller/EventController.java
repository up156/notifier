package com.notifier.notifier.controller;

import com.notifier.notifier.dto.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("api/v1/notifier/events")
@Tag(name = "Event Controller", description = "Контроллер для работы с событиями")
public interface EventController {

    @Operation(summary = "Получить все события")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventDto.class))))
    })
    @GetMapping
    ResponseEntity<List<EventDto>> getAllEvents();

    @Operation(summary = "Получить событие по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Событие найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "404", description = "Событие не найдено")
    })
    @GetMapping("/{id}")
    ResponseEntity<EventDto> getEvent(@PathVariable Long id);

    @Operation(summary = "Создать новое событие")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Событие создано",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventDto.class)))
    })
    @PostMapping
    ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventCreateRequest event);

    @Operation(summary = "Обновить событие")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Событие обновлено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "404", description = "Событие не найдено")
    })
    @PutMapping("/{id}")
    ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody @Valid EventUpdateRequest event);

    @Operation(summary = "Удалить событие")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие удалено"),
            @ApiResponse(responseCode = "404", description = "Событие не найдено")
    })

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEvent(@PathVariable Long id);
}
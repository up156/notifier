package com.notifier.notifier.controller.impl;


import com.notifier.notifier.controller.EventController;
import com.notifier.notifier.dto.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;
import com.notifier.notifier.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventControllerImpl implements EventController {

    private final EventService eventService;

    @Override
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Override
    public ResponseEntity<EventDto> getEvent(Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @Override
    public ResponseEntity<EventDto> createEvent(EventCreateRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @Override
    public ResponseEntity<EventDto> updateEvent(Long id, EventUpdateRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteEvent(Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }
}

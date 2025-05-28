package com.notifier.notifier.service;

import com.notifier.notifier.dto.EventDto;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;

import java.util.List;

public interface EventService {
    List<EventDto> getAllEvents();
    EventDto getEvent(Long id);
    EventDto createEvent(EventCreateRequest request);
    EventDto updateEvent(Long id, EventUpdateRequest request);
    void deleteEvent(Long id);
}

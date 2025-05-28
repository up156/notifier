package com.notifier.notifier.service.impl;

import com.notifier.notifier.dto.EventDto;
import com.notifier.notifier.ex.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.notifier.notifier.mapper.EventMapper;
import com.notifier.notifier.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.notifier.notifier.repository.EventRepository;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;
import com.notifier.notifier.service.EventService;
import com.notifier.notifier.service.NotificationService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final NotificationService notificationService;

    private static final String EVENT_NOT_FOUND_MESSAGE = "Event not found: ";

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllEvents() {

        log.info("Event Service started get all events");
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEvent(Long id) {

        log.info("Event Service started get event by id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND_MESSAGE + id));
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public EventDto createEvent(EventCreateRequest request) {

        log.info("Event Service started create event with request: {}", request);

        Event event = eventMapper.toEntity(request);
        event = eventRepository.save(event);

        notificationService.handleEvent(event);

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long id, EventUpdateRequest request) {

        log.info("Event Service started update event with request: {}", request);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND_MESSAGE + id));
        eventMapper.updateEvent(request, event);
        event = eventRepository.save(event);

        notificationService.handleEvent(event);

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(EVENT_NOT_FOUND_MESSAGE + id);
        }
        eventRepository.deleteById(id);
    }
}

package com.notifier.notifier.service.impl;

import com.notifier.notifier.TestData;
import com.notifier.notifier.dto.EventDto;
import com.notifier.notifier.ex.NotFoundException;
import com.notifier.notifier.mapper.EventMapper;
import com.notifier.notifier.mapper.EventMapperImpl;
import com.notifier.notifier.model.Event;
import com.notifier.notifier.repository.EventRepository;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;
import com.notifier.notifier.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.notifier.notifier.TestData.generateEvent;
import static com.notifier.notifier.TestData.generateEventCreateRequest;
import static com.notifier.notifier.TestData.generateEventDto;
import static com.notifier.notifier.TestData.generateEventDtoList;
import static com.notifier.notifier.TestData.generateEventList;
import static com.notifier.notifier.TestData.generateEventUpdateRequest;
import static com.notifier.notifier.TestData.generateExistingEvent;
import static com.notifier.notifier.TestData.generateUpdatedEvent;
import static com.notifier.notifier.TestData.generateUpdatedEventDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private NotificationService notificationService;

    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        EventMapper eventMapper = new EventMapperImpl();
        eventService = new EventServiceImpl(eventRepository, eventMapper, notificationService);
    }

    @Test
    void getAllEventsWhenRepositoryReturnsEventsThenSuccess() {

        List<Event> events = generateEventList();
        List<EventDto> eventDtos = generateEventDtoList();

        when(eventRepository.findAll()).thenReturn(events);

        List<EventDto> result = eventService.getAllEvents();

        assertEquals(eventDtos, result);
        verify(eventRepository).findAll();
    }


    @Test
    void getEventWhenRepositoryReturnsEventThenSuccess() {

        Event event = generateEvent();
        EventDto expectedDto = generateEventDto();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventDto result = eventService.getEvent(1L);

        assertEquals(expectedDto, result);
        verify(eventRepository).findById(1L);
    }

    @Test
    void getEventWhenRepositoryReturnsNoEventThenThrow() {

        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> eventService.getEvent(99L));
        assertTrue(ex.getMessage().contains("Event not found"));
        verify(eventRepository).findById(99L);
    }

    @Test
    void createEventWhenValidRequestThenSaveAndNotify() {

        EventCreateRequest request = generateEventCreateRequest();
        Event event = generateEvent();
        EventDto expectedDto = generateEventDto();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.createEvent(request);

        assertEquals(expectedDto, result);
        verify(eventRepository).save(any(Event.class));
        verify(notificationService).handleEvent(any(Event.class));
    }

    @Test
    void updateEventWhenRepositoryReturnsEventThenSuccess() {

        EventUpdateRequest request = generateEventUpdateRequest();
        Event existing = generateExistingEvent();
        Event updated = generateUpdatedEvent();
        EventDto expectedDto = generateUpdatedEventDto();
        when(eventRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenReturn(updated);

        EventDto result = eventService.updateEvent(2L, request);

        assertEquals(expectedDto, result);
        verify(eventRepository).findById(2L);
        verify(eventRepository).save(any(Event.class));
        verify(notificationService).handleEvent(any(Event.class));
    }

    @Test
    void updateEventWhenRepositoryReturnsNoEventThenThrow() {

        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        EventUpdateRequest request = TestData.generateEventUpdateRequest();
        NotFoundException ex = assertThrows(NotFoundException.class, () -> eventService.updateEvent(99L, request));
        assertTrue(ex.getMessage().contains("Event not found"));
        verify(eventRepository).findById(99L);
    }

    @Test
    void deleteEventWhenExistsThenDelete() {

        when(eventRepository.existsById(5L)).thenReturn(true);

        eventService.deleteEvent(5L);

        verify(eventRepository).existsById(5L);
        verify(eventRepository).deleteById(5L);
    }

    @Test
    void deleteEventWhenNotExistsThenThrow() {

        when(eventRepository.existsById(100L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> eventService.deleteEvent(100L));
        assertTrue(ex.getMessage().contains("Event not found"));
        verify(eventRepository).existsById(100L);
        verify(eventRepository, never()).deleteById(anyLong());
    }
}
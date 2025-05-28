package com.notifier.notifier;

import com.notifier.notifier.dto.EventDto;
import com.notifier.notifier.enums.NotificationStatus;
import com.notifier.notifier.model.Event;
import com.notifier.notifier.model.Notification;
import com.notifier.notifier.model.NotificationPeriod;
import com.notifier.notifier.model.User;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

public class TestData {

    public static List<Event> generateEventList() {
        Event event1 = Event.builder().id(1L).message("Event 1").eventDateTime(LocalDateTime.of(2025, Month.MAY, 28, 15, 0, 0)).build();
        Event event2 = Event.builder().id(2L).message("Event 2").eventDateTime(LocalDateTime.of(2025, Month.MAY, 29, 15, 0, 0)).build();
        return List.of(event1, event2);
    }

    public static List<EventDto> generateEventDtoList() {
        EventDto dto1 = EventDto.builder().id(1L).message("Event 1").eventDateTime(LocalDateTime.of(2025, Month.MAY, 28, 15, 0, 0)).build();
        EventDto dto2 = EventDto.builder().id(2L).message("Event 2").eventDateTime(LocalDateTime.of(2025, Month.MAY, 29, 15, 0, 0)).build();
        return List.of(dto1, dto2);
    }

    public static Event generateEvent() {
        return generateEvent(1L, "Event 1", LocalDateTime.of(2025, 5, 29, 12, 0));
    }

    public static Event generateUpdatedEvent() {
        return generateEvent(2L, "Updated event", LocalDateTime.of(2025, 5, 30, 9, 0));
    }

    public static Event generateExistingEvent() {
        return generateEvent(2L, "Old event", LocalDateTime.of(2025, 5, 29, 12, 0));
    }

    public static EventDto generateEventDto() {
        return generateEventDto(1L, "Event 1", LocalDateTime.of(2025, 5, 29, 12, 0));
    }

    public static EventDto generateExpectedEventDto() {
        return generateEventDto(1L, "Event 1", LocalDateTime.of(2025, 5, 28, 15, 0));
    }

    public static EventDto generateCreatedEventDto() {
        return generateEventDto(3L, "Event 1", LocalDateTime.of(2025, 5, 29, 15, 0));
    }

    public static EventDto generateUpdatedEventDto() {
        return generateEventDto(2L, "Updated event", LocalDateTime.of(2025, 5, 30, 9, 0));
    }

    private static Event generateEvent(Long id, String message, LocalDateTime dateTime) {
        return Event.builder()
                .id(id)
                .message(message)
                .eventDateTime(dateTime)
                .build();
    }

    private static EventDto generateEventDto(Long id, String message, LocalDateTime dateTime) {
        return EventDto.builder()
                .id(id)
                .message(message)
                .eventDateTime(dateTime)
                .build();
    }

    public static EventCreateRequest generateEventCreateRequest() {
        return new EventCreateRequest("Event 1", LocalDateTime.of(2025, 5, 29, 15, 0));
    }

    public static EventUpdateRequest generateEventUpdateRequest() {
        return new EventUpdateRequest("Updated event", LocalDateTime.of(2025, 5, 30, 9, 0));
    }

    public static User generateUserWithPeriodIncludingEvent() {

        Event event = generateEvent();

        NotificationPeriod period = NotificationPeriod.builder()
                .id(100L)
                .dayOfWeek(event.getEventDateTime().getDayOfWeek())
                .timeFrom(event.getEventDateTime().toLocalTime().minusHours(1))
                .timeTo(event.getEventDateTime().toLocalTime().plusHours(1))
                .build();

        User user = User.builder()
                .id(100L)
                .name("Test User")
                .periods(List.of(period))
                .build();

        period.setUser(user);

        return user;
    }

    public static User generateUserWithPeriodNotIncludingEvent() {

        NotificationPeriod period = NotificationPeriod.builder()
                .id(200L)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .timeFrom(LocalTime.of(8, 0))
                .timeTo(LocalTime.of(9, 0))
                .build();

        User user = User.builder()
                .id(200L)
                .name("User Not In Period")
                .periods(List.of(period))
                .build();

        period.setUser(user);
        return user;
    }

    public static Notification generateNotification() {
        Event event = generateEvent();
        User user = generateUserWithPeriodIncludingEvent();
        return Notification.builder()
                .id(1L)
                .user(user)
                .event(event)
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.of(2025, 5, 29, 13, 0))
                .scheduledFor(event.getEventDateTime())
                .build();
    }
}

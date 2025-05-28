package com.notifier.notifier.service.impl;

import com.notifier.notifier.dto.NotificationWsDto;
import com.notifier.notifier.enums.NotificationStatus;
import com.notifier.notifier.ex.NotFoundException;
import com.notifier.notifier.model.Event;
import com.notifier.notifier.model.Notification;
import com.notifier.notifier.model.NotificationPeriod;
import com.notifier.notifier.model.User;
import com.notifier.notifier.repository.NotificationRepository;
import com.notifier.notifier.repository.UserRepository;
import com.notifier.notifier.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void handleEvent(Event event) {

        log.info("Notification Service started handle event");
        notificationRepository.deleteByEventAndStatus(event, NotificationStatus.WAITING);

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (isEventInPeriod(event, user)) {

                Notification notification = Notification.builder()
                        .user(user)
                        .event(event)
                        .status(NotificationStatus.SENT)
                        .scheduledFor(event.getEventDateTime())
                        .sentAt(LocalDateTime.now())
                        .build();

                notifyUsers(notification);
                notificationRepository.save(notification);

                log.info("{} Пользователю {} отправлено оповещение с текстом: {}",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                        user.getName(), event.getMessage());
            } else {

                NotificationPeriod nextPeriod = findNextPeriod(event, user);
                if (nextPeriod != null) {
                    LocalDateTime nextInformingTime = calculateNextInformingTime(event, nextPeriod);
                    Notification notification = Notification.builder()
                            .user(user)
                            .event(event)
                            .status(NotificationStatus.WAITING)
                            .scheduledFor(nextInformingTime)
                            .build();
                    notificationRepository.save(notification);
                }
            }
        }
    }

    @Override
    public void notifyUsers(Notification notification) {

        log.info("Notification Service started notify users with notification: {}", notification);
        messagingTemplate.convertAndSend("/topic/notifications", toNotificationWsDto(notification));
    }

    @Transactional
    @Scheduled(initialDelay = 1000, fixedDelay = 60_000)
    public void processWaitingNotifications() {

        log.info("Notification Service started process waiting notifications");

        LocalDateTime now = LocalDateTime.now();

        List<Notification> waitingNotifications = notificationRepository.findAllByStatusAndScheduledForLessThanEqual(NotificationStatus.WAITING, now);

        for (Notification notification : waitingNotifications) {

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(now);

            messagingTemplate.convertAndSend("/topic/notifications", toNotificationWsDto(notification));

            log.info("В {} Пользователю {} отправлено оповещение с текстом: {}",
                    now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                    notification.getUser().getName(),
                    notification.getEvent().getMessage());
        }

        notificationRepository.saveAll(waitingNotifications);
    }

    private NotificationWsDto toNotificationWsDto(Notification notification) {
        return NotificationWsDto.builder()
                .eventId(notification.getEvent().getId())
                .message(notification.getEvent().getMessage())
                .sentTo(notification.getUser().getName())
                .sentAt(notification.getSentAt())
                .build();
    }

    private boolean isEventInPeriod(Event event, User user) {

        DayOfWeek eventDay = event.getEventDateTime().getDayOfWeek();
        LocalTime eventTime = event.getEventDateTime().toLocalTime();

        return user.getPeriods()
                .stream()
                .anyMatch(notificationPeriod ->
                        eventDay.equals(notificationPeriod.getDayOfWeek()) &&
                        eventTime.isAfter(notificationPeriod.getTimeFrom()) &&
                        eventTime.isBefore(notificationPeriod.getTimeTo())
                );
    }

    private NotificationPeriod findNextPeriod(Event event, User user) {

        DayOfWeek eventDay = event.getEventDateTime().getDayOfWeek();
        LocalTime eventTime = event.getEventDateTime().toLocalTime();

        List<NotificationPeriod> notificationPeriods = user.getPeriods()
                .stream()
                .sorted(Comparator.comparing(NotificationPeriod::getDayOfWeek)
                        .thenComparing(NotificationPeriod::getTimeFrom))
                .toList();

        for (NotificationPeriod period : notificationPeriods) {

            if (eventDay.equals(period.getDayOfWeek()) && eventTime.isBefore(period.getTimeFrom())) {
                return period;
            }
            if (eventDay.getValue() < period.getDayOfWeek().getValue()) {
                return period;
            }
        }
        return notificationPeriods.stream().findFirst().orElseThrow(() -> new NotFoundException("period not found"));
    }

    private LocalDateTime calculateNextInformingTime(Event event, NotificationPeriod nextPeriod) {

        int eventDayCount = event.getEventDateTime().getDayOfWeek().getValue();
        int nextPeriodCount = nextPeriod.getDayOfWeek().getValue();
        int plusDays = 0;
        if (eventDayCount != nextPeriodCount) {
            plusDays = eventDayCount >= nextPeriodCount ? 7 - eventDayCount + nextPeriodCount : nextPeriodCount - eventDayCount;
        } else if (nextPeriod.getTimeFrom().isBefore(event.getEventDateTime().toLocalTime())) {
            plusDays = 7;
        }
        return LocalDateTime.of(event.getEventDateTime().toLocalDate(), nextPeriod.getTimeFrom()).plusDays(plusDays);
    }
}


package com.notifier.notifier.service.impl;

import com.notifier.notifier.TestData;
import com.notifier.notifier.enums.NotificationStatus;
import com.notifier.notifier.model.Event;
import com.notifier.notifier.model.Notification;
import com.notifier.notifier.model.User;
import com.notifier.notifier.repository.NotificationRepository;
import com.notifier.notifier.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static com.notifier.notifier.TestData.generateEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                notificationRepository,
                userRepository,
                messagingTemplate
        );
    }

    @Test
    void handleEventWhenEventInPeriodShouldNotifyAndSaveSent() {

        Event event = generateEvent();
        User user = TestData.generateUserWithPeriodIncludingEvent();

        when(userRepository.findAll()).thenReturn(List.of(user));

        notificationService.handleEvent(event);

        verify(messagingTemplate).convertAndSend(eq("/topic/notifications"), (Object) any());
        verify(notificationRepository).deleteByEventAndStatus(event, NotificationStatus.WAITING);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();

        assertEquals(event.getId(), saved.getEvent().getId());
        assertEquals(NotificationStatus.SENT, saved.getStatus());
        assertNotNull(saved.getSentAt());
    }

    @Test
    void handleEventWhenEventNotInPeriodShouldSaveWaiting() {
        Event event = generateEvent();
        User user = TestData.generateUserWithPeriodNotIncludingEvent();

        when(userRepository.findAll()).thenReturn(List.of(user));

        notificationService.handleEvent(event);

        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
        verify(notificationRepository).deleteByEventAndStatus(event, NotificationStatus.WAITING);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();

        assertEquals(event.getId(), saved.getEvent().getId());
        assertEquals(NotificationStatus.WAITING, saved.getStatus());
        assertNull(saved.getSentAt());
        assertNotNull(saved.getScheduledFor());
    }

    @Test
    void notifyUsersShouldSendWebSocketMessage() {

        Notification notification = TestData.generateNotification();

        notificationService.notifyUsers(notification);
        verify(messagingTemplate).convertAndSend(eq("/topic/notifications"), (Object) any());
    }
}
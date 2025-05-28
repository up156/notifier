package com.notifier.notifier.repository;

import com.notifier.notifier.enums.NotificationStatus;
import com.notifier.notifier.model.Event;
import com.notifier.notifier.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByEventAndStatus(Event event, NotificationStatus notificationStatus);

    List<Notification> findAllByStatusAndScheduledForLessThanEqual(NotificationStatus notificationStatus, LocalDateTime now);
}

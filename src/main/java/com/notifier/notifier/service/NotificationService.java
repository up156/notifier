package com.notifier.notifier.service;

import com.notifier.notifier.model.Event;
import com.notifier.notifier.model.Notification;

public interface NotificationService {

    void handleEvent(Event event);

    void notifyUsers(Notification notification);
}

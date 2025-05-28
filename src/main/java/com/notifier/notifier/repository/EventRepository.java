package com.notifier.notifier.repository;

import com.notifier.notifier.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}

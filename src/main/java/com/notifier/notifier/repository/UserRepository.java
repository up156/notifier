package com.notifier.notifier.repository;

import com.notifier.notifier.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

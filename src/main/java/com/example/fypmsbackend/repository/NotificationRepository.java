package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Notification;
import com.example.fypmsbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}

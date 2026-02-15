package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisorProfileRepository extends JpaRepository<SupervisorProfile, Long> {
    Optional<SupervisorProfile> findByUserId(Long userId);
    Optional<SupervisorProfile> findByUser(User user);
}

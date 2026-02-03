package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.SupervisorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisorProfileRepository extends JpaRepository<SupervisorProfile, Long> {
    Optional<SupervisorProfile> findByUserId(Long userId);
}

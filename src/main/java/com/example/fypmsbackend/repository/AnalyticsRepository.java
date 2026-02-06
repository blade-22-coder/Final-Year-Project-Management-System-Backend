package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Analytics findByStudentProfileId(Long studentProfileId);
}

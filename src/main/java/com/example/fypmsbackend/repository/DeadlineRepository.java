package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadlineRepository extends JpaRepository<Deadline, Long> {
}

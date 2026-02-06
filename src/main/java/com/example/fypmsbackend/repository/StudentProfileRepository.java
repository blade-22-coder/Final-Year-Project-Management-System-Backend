package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.User;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    List<StudentProfile> findBySupervisorUserEmail(String supervisorEmail);

    List<StudentProfile>findBySupervisor(User supervisor);
}

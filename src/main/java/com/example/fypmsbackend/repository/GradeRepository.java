package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Grade;
import com.example.fypmsbackend.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Grade findByStudentProfileId(Long studentProfileId);
}

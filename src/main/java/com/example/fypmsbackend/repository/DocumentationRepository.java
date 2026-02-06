package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Documentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentationRepository extends JpaRepository<Documentation, Long> {
    List<Documentation> findByStudentProfileId(Long studentProfileId);
}

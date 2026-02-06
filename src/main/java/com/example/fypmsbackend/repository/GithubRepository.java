package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Github;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepository extends JpaRepository<Github, Long> {
    Github findByStudentProfileId(Long studentProfileId);
}

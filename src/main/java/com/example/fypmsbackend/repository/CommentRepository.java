package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countBySubmissionStudentProfileId(Long studentProfileId);
}

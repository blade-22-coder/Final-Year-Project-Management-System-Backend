package com.example.fypmsbackend.grade;

import com.example.fypmsbackend.submission.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findBySubmission(Submission submission);
    Optional<Grade> findByStudentProfile_Id(Long studentProfileId);}

package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Submission;
import com.example.fypmsbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    long countByStudentProfileId(Long studentProfileId);

    @Query("""
        SELECT s FROM Submission s WHERE s.studentProfile.id = :id ORDER BY s.submittedAt DESC LIMIT 1
    """)
    Submission findLatestSubmission(@Param("id") Long id);

    @Query("""
        SELECT EXTRACT(MONTH FROM s.submittedAt) AS month,
               COUNT(s) AS count
        FROM Submission s
        GROUP BY EXTRACT(MONTH FROM s.submittedAt)
        ORDER BY month
    """)
    List<Object[]> monthlySubmissions();
    List<Submission> findByStudentId(Long studentId);
}


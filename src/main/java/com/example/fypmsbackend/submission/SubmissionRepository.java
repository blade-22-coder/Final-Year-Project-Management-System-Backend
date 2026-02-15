package com.example.fypmsbackend.submission;

import com.example.fypmsbackend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // ✅ Count submissions for a student
    long countByStudentProfileId(Long studentProfileId);

    // ✅ Get the latest submission (single) for status/battery
    @Query("""
        SELECT s FROM Submission s 
        WHERE s.studentProfile.id = :id 
        ORDER BY s.submittedAt DESC
    """)
    List<Submission> findAllByStudentProfileIdOrderBySubmittedAtDesc(@Param("id") Long id);

    // Optional convenience method to get only the latest submission
    default Submission findLatestSubmission(Long id) {
        List<Submission> list = findAllByStudentProfileIdOrderBySubmittedAtDesc(id);
        if(list.isEmpty()) throw new RuntimeException("Submission not found");
        return list.get(0);
    }

    // ✅ For supervisor / admin: get all submissions for a student
    List<Submission> findByStudentProfileId(Long studentId);

    // ✅ Optional: get one submission by student ID (if needed)
    @Query("SELECT s FROM Submission s WHERE s.studentProfile.id = :studentId ORDER BY s.submittedAt DESC")
    Optional<Submission> findLatestByStudentProfileId(Long studentId);

    // ✅ Monthly submissions stats
    @Query("""
        SELECT EXTRACT(MONTH FROM s.submittedAt) AS month,
               COUNT(s) AS count
        FROM Submission s
        GROUP BY EXTRACT(MONTH FROM s.submittedAt)
        ORDER BY month
    """)
    List<Object[]> monthlySubmissions();
    List<Submission> findBySubmittedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(Status status);
    long countByProposalSubmittedTrue();
    long countByFinalReportSubmittedTrue();


}

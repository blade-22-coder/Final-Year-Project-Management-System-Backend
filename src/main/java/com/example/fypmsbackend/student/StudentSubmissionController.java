package com.example.fypmsbackend.student;

import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/student/submissions")
public class StudentSubmissionController {

    private final SubmissionRepository submissionRepo;
    private final StudentProfileRepository studentProfileRepo;

    public StudentSubmissionController(SubmissionRepository submissionRepo,
                                       StudentProfileRepository studentProfileRepo) {
        this.submissionRepo = submissionRepo;
        this.studentProfileRepo = studentProfileRepo;
    }

    /* ---------- helper ---------- */
    private Submission getOrCreateSubmission(Long studentId) {
        return submissionRepo.findLatestByStudentProfileId(studentId)
                .orElseGet(() -> {
                    StudentProfile student = studentProfileRepo.findById(studentId)
                            .orElseThrow(() -> new RuntimeException("Student not found"));

                    Submission sub = new Submission();
                    sub.setStudentProfile(student);
                    sub.setSubmittedAt(LocalDateTime.now());
                    return submissionRepo.save(sub);
                });
    }

    /* ---------- title ---------- */
    @PostMapping("/{studentId}/title")
    public Submission submitTitle(@PathVariable Long studentId,
                                  @RequestParam String title) {

        Submission sub = getOrCreateSubmission(studentId);
        sub.setProjectTitle(title);
        sub.setTitleSubmitted(true);

        return submissionRepo.save(sub);
    }

    /* ---------- github ---------- */
    @PostMapping("/{studentId}/github")
    public Submission saveGitHub(@PathVariable Long studentId,
                                 @RequestParam String githubLink) {

        Submission sub = getOrCreateSubmission(studentId);
        sub.setGithubLink(githubLink);
        sub.setGithubLinkSubmitted(true);

        return submissionRepo.save(sub);
    }

    /* ---------- proposal ---------- */
    @PostMapping("/{studentId}/proposal")
    public Submission uploadProposal(@PathVariable Long studentId,
                                     @RequestParam MultipartFile file) throws IOException {

        Submission sub = getOrCreateSubmission(studentId);

        String path = "uploads/proposals/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(path));

        sub.setProposalUrl(path);
        sub.setProposalSubmitted(true);

        return submissionRepo.save(sub);
    }

    /* ---------- final report ---------- */
    @PostMapping("/{studentId}/finalReport")
    public Submission uploadFinalReport(@PathVariable Long studentId,
                                        @RequestParam MultipartFile file) throws IOException {

        Submission sub = getOrCreateSubmission(studentId);

        String path = "uploads/finalReports/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(path));

        sub.setFinalReportUrl(path);
        sub.setFinalReportSubmitted(true);

        return submissionRepo.save(sub);
    }

    /* ---------- snapshots ---------- */
    @PostMapping("/{studentId}/snapshots")
    public Submission uploadSnapshots(@PathVariable Long studentId,
                                      @RequestParam MultipartFile file) throws IOException {

        Submission sub = getOrCreateSubmission(studentId);

        String path = "uploads/snapshots/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(path));

        sub.setSnapshotsSubmitted(true);

        return submissionRepo.save(sub);
    }

    /* ---------- status ---------- */
    @GetMapping("/{studentId}")
    public Submission getStatus(@PathVariable Long studentId) {
        return submissionRepo.findLatestByStudentProfileId(studentId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }
}

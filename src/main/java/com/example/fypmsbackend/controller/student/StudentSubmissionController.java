package com.example.fypmsbackend.controller.student;

import com.example.fypmsbackend.controller.SubmissionController;
import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.Submission;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.SubmissionRepository;
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

    public StudentSubmissionController(SubmissionRepository submissionRepo, StudentProfileRepository studentProfileRepo) {
        this.submissionRepo = submissionRepo;
        this.studentProfileRepo = studentProfileRepo;
    }

    @PostMapping("/{studentId}/title")
    public Submission submitTitle(
            @PathVariable Long studentId,
            @RequestParam String title) {

        StudentProfile student = studentProfileRepo.findById(studentId).orElseThrow();

        Submission sub = submissionRepo.findByStudentProfileId(studentId);
        if(sub == null) {
            sub = new Submission();
            sub.setStudentProfile(student);
        }

        sub.setProjectTitle(title);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }

    @PostMapping("/{studentId}/github")
    public Submission saveGitHub(
            @PathVariable Long studentId,
            @RequestParam String githubLink) {

        Submission sub = submissionRepo
                .findByStudentProfileId(studentId);

        sub.setGithubLink(githubLink);
        sub.setRepoSubmitted(true);

        return submissionRepo.save(sub);
    }

    @PostMapping("/{studentId}/proposal")
    public Submission uploadProposal(
            @PathVariable Long studentId,
            @RequestParam MultipartFile file) throws IOException {

        Submission sub = submissionRepo.findByStudentProfileId(studentId);

        String path = "uploads/proposals/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(path));

        sub.setProposalUrl(path);
        sub.setProposalSubmitted(true);

        return submissionRepo.save(sub);
    }

    @PostMapping("/{studentId}/finalReport")
    public Submission uploadFinalReport(
            @PathVariable Long studentId,
            @RequestParam MultipartFile file) throws IOException {

        Submission sub = submissionRepo.findByStudentProfileId(studentId);

        String path = "uploads/finalReports/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(path));

        sub.setFinalReportUrl(path);
        sub.setFinalReportSubmitted(true);

        return submissionRepo.save(sub);
    }

    @PostMapping("/{studentId}/snapshot")
    public Submission uploadSnapshot(
            @PathVariable Long studentId,
            @RequestParam MultipartFile file) throws IOException {

        Submission sub = submissionRepo.findByStudentProfileId(studentId);

        String path = "uploads/snapshots/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(path));

        sub.setSnapshotUrl(path);
        sub.setProposalSubmitted(true);

        return submissionRepo.save(sub);
    }

    @GetMapping("/{studentId}")
    public Submission getStatus(@PathVariable Long studentId) {
        return submissionRepo.findByStudentProfileId(studentId);
    }
}

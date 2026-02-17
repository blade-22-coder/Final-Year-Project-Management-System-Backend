package com.example.fypmsbackend.submission;

import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    private static final String BASE_DIR = "uploads/submissions/";

    private final SubmissionRepository submissionRepo;
    private final StudentProfileRepository studentRepo;
    private final UserRepository userRepo;

    private static final String UPLOAD_DIR = "uploads/";

    public SubmissionService(SubmissionRepository submissionRepo,
                             StudentProfileRepository studentRepo,
                             UserRepository userRepo) {
        this.submissionRepo = submissionRepo;
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
    }

    // -------------------- Upload Files --------------------
    public Submission uploadFile(Long studentId, MultipartFile file, String type) {
        try {
            // ensure directory exists
            String folder = switch (type) {
                case "proposal" -> "proposals/";
                case "finalReport" -> "finalReports/";
                case "snapshot" -> "snapshots/";
                default -> "";
            };
            Files.createDirectories(Paths.get(UPLOAD_DIR + folder));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + folder + fileName);
            Files.write(path, file.getBytes());

            StudentProfile student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // get existing submission or create new
            Submission sub = submissionRepo.findLatestByStudentProfileId(studentId)
                    .orElse(new Submission());
            sub.setStudentProfile(student);
            sub.setSubmittedAt(LocalDateTime.now());

            // assign file based on type
            switch (type) {
                case "proposal" -> {
                    sub.setProposalUrl(path.toString());
                    sub.setProposalSubmitted(true);
                }
                case "finalReport" -> {
                    sub.setFinalReportUrl(path.toString());
                    sub.setFinalReportSubmitted(true);
                }
                case "snapshot" -> {
                    sub.setSnapshotsUrl(path.toString());
                }
                default -> throw new RuntimeException("Invalid file type");
            }

            return submissionRepo.save(sub);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    // -------------------- Submit Title --------------------
    public Submission submitTitle(Long studentId, String title) {
        StudentProfile student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Submission sub = submissionRepo.findLatestByStudentProfileId(studentId)
                .orElseGet(() -> {
                    Submission newSub = new Submission();
                    newSub.setStudentProfile(student);
                    return newSub;
                        });

        sub.setProjectTitle(title);
        sub.setTitleSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }

    // -------------------- Save GitHub Link --------------------
    public Submission saveGithubLink(Long studentId, String githubLink) {
        Submission sub = submissionRepo.findLatestByStudentProfileId(studentId)
                .orElseThrow(() -> new RuntimeException("No submission found for student"));

        sub.setGithubLink(githubLink);
        sub.setGithubLinkSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }

    // -------------------- Get Submissions --------------------
    public Submission getLatestSubmission(Long studentId) {
        return submissionRepo.findLatestSubmission(studentId);
    }

    public void submit(MultipartFile file, Authentication auth) {
    }
}

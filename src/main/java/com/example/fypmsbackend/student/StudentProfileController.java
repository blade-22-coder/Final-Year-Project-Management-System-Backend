package com.example.fypmsbackend.student;

import com.example.fypmsbackend.security.AuthHelper;
import com.example.fypmsbackend.service.BatteryService;
import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final AuthHelper authHelper;

    private UserRepository userRepo;
    private StudentProfileRepository studentProfileRepo;
    private SubmissionRepository submissionRepo;
    private final BatteryService  batteryService;

    //HELPER METHODS
    private StudentProfile getCurrentStudentProfile() {
        User user = authHelper.getCurrentUser();
        return studentProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    private Submission getOrCreateSubmission(StudentProfile student) {
        return submissionRepo.findLatestByStudentProfileId(student.getId())
                .orElseGet(() -> {
                    Submission sub = new Submission();
                    sub.setStudentProfile(student);
                    sub.setSubmittedAt(LocalDateTime.now());
                    return submissionRepo.save(sub);
                });
    }

    //PROFILE
    @PostMapping("/onboard")
    public ResponseEntity<?> onboardStudent(
            @RequestBody StudentOnboardingRequest req,
            Authentication auth) {

        User user = authHelper.getCurrentUser();

        if (user.isOnboarded())
            return ResponseEntity.badRequest().body("Already onboarded");

        StudentProfile profile  = new StudentProfile();
        profile.setUser(user);
        profile.setRegistrationNumber(req. registrationNumber());
        profile.setCourse(req.course());
        profile.setProjectTitle(req.projectTitle());

        studentProfileRepo.save(profile);

        user.setOnboarded(true);
        userRepo.save(user);

        return ResponseEntity.ok("Student Onboarded");
    }
    @GetMapping("/me")
    public StudentProfile myProfile() {
        return getCurrentStudentProfile();
    }
    @Value("${file.upload-dir")
    private String uploadDir;
    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @RequestBody("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws IOException {

        StudentProfile student = studentProfileRepo.findByUser(user)
                .orElseThrow(()-> new RuntimeException("Supervisor Not Found"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        student.setProfileImagePath(fileName);
        studentProfileRepo.save(student);

        return ResponseEntity.ok("Profile Image Updated");
    }
    @GetMapping("/profile-image/{fileName}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String fileName) throws IOException {

        Path path = Paths.get(uploadDir).resolve(fileName);
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    //SUBMISSIONS
    @PostMapping("/submit/title")
    public Submission submitTitle(@RequestParam String title) {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        sub.setProjectTitle(title);
        sub.setTitleSubmitted(true);

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/github")
    public Submission submitGitHub(@RequestParam String githubLink) {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        sub.setProjectTitle(githubLink);
        sub.setTitleSubmitted(true);

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/proposal")
    public Submission uploadProposal(@RequestParam MultipartFile file) throws IOException {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        String path = "uploads/proposals/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Path.of(path));

        sub.setProposalUrl(path);
        sub.setProposalSubmitted(true);

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/finalReport")
    public Submission uploadFinalReport(@RequestParam MultipartFile file) throws IOException {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        String path = "uploads/finalReports/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Path.of(path));

        sub.setFinalReportUrl(path);
        sub.setFinalReportSubmitted(true);

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/snapshots")
    public Submission uploadSnapshots(@RequestParam MultipartFile file) throws IOException {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        String path = "uploads/snapshots/" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Path.of(path));

        sub.setSnapshotsUrl(path);
        sub.setSnapshotsSubmitted(true);

        return submissionRepo.save(sub);
    }
    @GetMapping("/submission")
    public Submission getMySubmission() {
        StudentProfile student = getCurrentStudentProfile();
        return submissionRepo.findLatestByStudentProfileId(student.getId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }

    //STATUS
    @GetMapping("/status")
    public Map<String, Object> getStatus() {

        StudentProfile student = getCurrentStudentProfile();

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(student.getId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        int battery = batteryService.calculateBattery(sub);

        return Map.of(
                "battery", battery,
                "titleApproved", sub.isTitleApproved(),
                "proposalApproved", sub.isProposalApproved(),
                "finalReportApproved", sub.isFinalReportApproved(),
                "githubLinkApproved", sub.isGithubLinkApproved(),
                "snapshotsApproved", sub.isSnapshotsApproved()
        );
    }
}

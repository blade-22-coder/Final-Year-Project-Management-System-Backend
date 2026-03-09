package com.example.fypmsbackend.student;

import com.example.fypmsbackend.deadline.Deadline;
import com.example.fypmsbackend.deadline.DeadlineRepository;
import com.example.fypmsbackend.model.Notification;
import com.example.fypmsbackend.repository.NotificationRepository;
import com.example.fypmsbackend.security.AuthHelper;
import com.example.fypmsbackend.service.BatteryService;
import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final AuthHelper authHelper;

    private final UserRepository userRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final SubmissionRepository submissionRepo;
    private final BatteryService  batteryService;
    private final NotificationRepository notificationRepository;
    private final DeadlineRepository deadlineRepo;

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
    @Value("${file.upload-dir}")
    private String uploadDir;
    @PutMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) throws IOException {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow();

        StudentProfile student = studentProfileRepo.findByUser(user)
                .orElseThrow(()-> new RuntimeException("Student Not Found"));

        //validate file type
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body("Only JPG, PNG, WEBP allowed");
        }

        //validate size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File is too large(max 5MB)");
        }

        //create folder if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        //delete old image
        if (student.getProfileImagePath() != null) {
            Path oldFile = uploadPath.resolve(student.getProfileImagePath());
            Files.deleteIfExists(oldFile);
        }

        //save new file
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        student.setProfileImagePath(fileName);
        studentProfileRepo.save(student);

        return ResponseEntity.ok("Profile Image Updated");
    }
    @GetMapping("/profile-image/{fileName}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String fileName) throws IOException {

        Path path = Paths.get(uploadDir).resolve(fileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    //SUBMISSIONS
    @PostMapping("/submit/title")
    public Submission submitTitle(@RequestParam String title) {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        sub.setProjectTitle(title);
        sub.setTitleSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/github")
    public Submission submitGitHub(@RequestParam String githubLink) {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        sub.setGithubLink(githubLink);
        sub.setGithubLinkSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/proposal")
    public Submission uploadProposal(@RequestParam MultipartFile file) throws IOException {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        Path uploadPath = Paths.get("uploads/proposals");
        if(!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String path = UUID.randomUUID()+"_"+file.getOriginalFilename();
        Path filePath = uploadPath.resolve(path);

        Files.copy(file.getInputStream(), filePath);

        sub.setProposalUrl(path);
        sub.setProposalSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/finalReport")
    public Submission uploadFinalReport(@RequestParam MultipartFile file) throws IOException {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        Path uploadPath = Paths.get("uploads/finalReports");
        if(!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String path = UUID.randomUUID()+"_"+ file.getOriginalFilename();
        Path filePath = uploadPath.resolve(path);

        Files.copy(file.getInputStream(), filePath);

        sub.setFinalReportUrl(path);
        sub.setFinalReportSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(sub);
    }
    @PostMapping("/submit/snapshots")
    public Submission uploadSnapshots(@RequestParam MultipartFile[] files ) throws IOException {

        StudentProfile student = getCurrentStudentProfile();
        Submission sub = getOrCreateSubmission(student);

        Path uploadPath = Paths.get("uploads/snapshots");
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        StringBuilder paths = new StringBuilder();

        String firstFile = null;

        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            if (firstFile == null) {
                firstFile = fileName;
                sub.setFileName(file.getOriginalFilename());
                sub.setFilePath(paths.toString());
            }
            paths.append(fileName).append(";");
        }

        sub.setSnapshotsUrl(paths.toString());
        sub.setSnapshotsSubmitted(true);
        sub.setSubmittedAt(LocalDateTime.now());

        if (files.length >  0) {
            sub.setFilePath(uploadPath.resolve(paths.toString().split(";")[0]).toString());
            sub.setFileName(files[0].getOriginalFilename());
        }

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
                "titleStatus", getStatus(sub.isTitleSubmitted(), sub.isTitleApproved()),
                "proposalStatus", getStatus(sub.isProposalSubmitted(), sub.isProposalApproved()),
                "finalReportStatus", getStatus(sub.isFinalReportSubmitted(),sub.isFinalReportApproved()),
                "githubLinkStatus", getStatus(sub.isGithubLinkSubmitted(), sub.isGithubLinkApproved()),
                "snapshotsStatus", getStatus(sub.isSnapshotsSubmitted(), sub.isSnapshotsApproved())
        );
    }
    private String getStatus(boolean submitted, boolean approved){
        if (!submitted) return "WAITING";
        if (submitted && !approved)
            return "PENDING";
        if (approved)
            return "APPROVED";
        return "REJECTED";
    }

    //COMMENTS
    @GetMapping("/comments/me")
    public ResponseEntity<?> getComments() {
        StudentProfile student = getCurrentStudentProfile();
        Submission sub = submissionRepo.findLatestByStudentProfileId(student.getId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        //assuming submission has a getComments() method
        return ResponseEntity.ok(
                sub.getComments() == null ? List.of() : sub.getComments()
        );
    }

    //NOTIFICATIONS
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        User user = authHelper.getCurrentUser();

        //fetch notifications from DB, ordered by latest
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(notifications);
    }

    //DEADLINES
    @GetMapping("/deadlines")
    public List<Deadline> getDeadlines() {
        return deadlineRepo.findAll();
    }

    @GetMapping("/files/{folder}/{fileName}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String folder,
            @PathVariable String fileName) throws IOException {

        Path path = Paths.get("uploads/"+folder).resolve(fileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String type = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type))
                .body(resource);
    }
}

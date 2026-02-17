package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.grade.Grade;
import com.example.fypmsbackend.grade.GradeRepository;
import com.example.fypmsbackend.model.Analytics;
import com.example.fypmsbackend.model.Comment;
import com.example.fypmsbackend.model.Documentation;
import com.example.fypmsbackend.model.Github;
import com.example.fypmsbackend.repository.AnalyticsRepository;
import com.example.fypmsbackend.repository.CommentRepository;
import com.example.fypmsbackend.repository.DocumentationRepository;
import com.example.fypmsbackend.repository.GithubRepository;
import com.example.fypmsbackend.security.AuthHelper;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorProfileController {

    private final AuthHelper authHelper;

    private final UserRepository userRepo;
    private final SupervisorProfileRepository supervisorProfileRepo;
    private final SubmissionRepository submissionRepo;
    private final CommentRepository commentRepo;
    private final DocumentationRepository documentationRepo;
    private final GithubRepository  githubRepo;
    private final AnalyticsRepository analyticsRepo;
    private final GradeRepository  gradeRepo;
    private final SupervisorStudentService supervisorStudentService;
    private final StudentProfileRepository studentRepo;


    //PROFILE
    @PostMapping("/onboard")
    public ResponseEntity<?> onboardSupervisor(
            @RequestBody SupervisorOnboardingRequest req,
            Authentication auth) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow();

        if (user.isOnboarded())
            return ResponseEntity.badRequest().body("Already onboarded");

            SupervisorProfile profile =  new SupervisorProfile();
            profile.setUser(user);
            profile.setStaffId(req.staffId());
            profile.setDepartment(req.department());
            profile.setMaxStudent(req.maxStudents());

            supervisorProfileRepo.save(profile);

            user.setOnboarded(true);
            userRepo.save(user);

            return ResponseEntity.ok("Supervisor Onboarded");
    }
    @GetMapping("/me")
    public SupervisorProfile myProfile(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        return supervisorProfileRepo.findByUserId(user.getId()).orElseThrow();

    }
    @Value("${file.upload-dir}")
    private String uploadDir;
    @PutMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication auth
            ) throws IOException {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow();

        SupervisorProfile supervisor = supervisorProfileRepo.findByUser(user)
                .orElseThrow(()-> new RuntimeException("Supervisor Not Found"));

        //validate file type
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body("Only JPG, PNG, WEBP allowed");
        }

        //validate size(5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File is too large (max 5MB)");
        }

        //create folder if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        //delete old image
        if (supervisor.getProfileImagePath() != null) {
            Path oldFile = uploadPath.resolve(supervisor.getProfileImagePath());
            Files.deleteIfExists(oldFile);
        }

        //save new file
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        supervisor.setProfileImagePath(fileName);
        supervisorProfileRepo.save(supervisor);

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

    //STUDENTS
    @GetMapping("/students")
    public List<Map<String, Object>> myStudents() {

        User supervisor = authHelper.getCurrentUser();

        List<StudentProfile> students =
                supervisorStudentService.getMyStudents();

        return students.stream().map(student -> {

            Submission sub = submissionRepo
                    .findByStudentProfileId(student.getId()).orElse(null);

            int approvedCount = 0;
            int total = 5; //title. proposal, report, GitHub, snapshots

            if (sub != null) {
                if (sub.isTitleApproved()) approvedCount++;
                if (sub.isProposalApproved()) approvedCount++;
                if (sub.isFinalReportApproved())  approvedCount++;
                if (sub.isGithubLinkApproved()) approvedCount++;
                if (sub.isSnapshotsApproved()) approvedCount++;
            }

            int progress = (approvedCount * 100) / total;

            Map<String, Object> map = new HashMap<>();
            map.put("id", student.getId());
            map.put("fullName", student.getFullName());
            map.put("registrationNumber", student.getRegistrationNumber());
            map.put("progress", progress);

            return map;

        }).collect(Collectors.toList());

    }

    //SUBMISSIONS
    @PostMapping("/approve/{submissionId}/{type}")
    public Submission approveSubmission(
            @PathVariable Long submissionId,
            @PathVariable String type) {

        Submission sub = submissionRepo.findById(submissionId).orElseThrow();

        switch(type) {
            case "title" -> sub.setTitleApproved(true);
            case "proposal" -> sub.setProposalApproved(true);
            case "finalReport" -> sub.setFinalReportApproved(true);
            case "githubLink" -> sub.setGithubLinkApproved(true);
            case "snapshots" -> sub.setSnapshotsApproved(true);
            default -> throw new RuntimeException("Invalid submission type");
        }

        return submissionRepo.save(sub);
    }

    //COMMENTS
    @PostMapping("/comments/{submissionId}")
    public Comment addComment(@PathVariable Long submissionId,
                              @RequestParam String message) {
        User supervisor = authHelper.getCurrentUser();
        Submission sub = submissionRepo.findById(submissionId).orElseThrow();

        Comment c = new Comment();
        c.setMessage(message);
        c.setSupervisor(supervisor);
        c.setSubmission(sub);
        c.setCreatedAt(LocalDateTime.now());

        return commentRepo.save(c);
    }
    @GetMapping("/comments/{submissionId}")
    public List<Comment> getComments(@PathVariable Long submissionId) {
        return commentRepo.findBySubmissionId(submissionId);
    }

    //DOCUMENTATION
    @GetMapping("/docs/{studentProfileId}")
    public List<Documentation> getDocs(@PathVariable Long studentProfileId) {
        return documentationRepo.findByStudentProfileId(studentProfileId);
    }
    @PostMapping("/docs/{docId}/comment")
    public Documentation commentOnDoc(@PathVariable Long docId,
                                      @RequestBody String comment) {
        Documentation doc = documentationRepo.findById(docId).orElseThrow();
        doc.setSupervisorComment(comment);
        return documentationRepo.save(doc);
    }

    //GITHUB
    @GetMapping("/github/{studentProfileId}")
    public Github getGithub(@PathVariable Long studentProfileId) {
        return githubRepo.findByStudentProfileId(studentProfileId);
    }
    @PostMapping("/github/{Id}/comment")
    public Github commentOnGithub(@PathVariable Long Id,
                                  @RequestBody String comment) {
        Github g  = githubRepo.findById(Id).orElseThrow();
        g.setSupervisorComment(comment);
        return githubRepo.save(g);
    }

    //GRADES
    @GetMapping("/grades/{studentId}")
    public Grade getGrade(@PathVariable Long studentId) {
        return gradeRepo.findByStudentProfileId(studentId);
    }
    @PostMapping("/grades/{studentId}")
    public Grade saveGrade(@PathVariable Long studentId,
                           @RequestBody Grade grade) {
        User supervisor = authHelper.getCurrentUser();

        StudentProfile student =
                studentRepo.findById(studentId).orElseThrow();

        grade.setStudentProfile(student);
        grade.setSupervisorProfile(supervisor);
        grade.setTotal(
                grade.getProposal() +
                        grade.getProgress() +
                        grade.getFinalReport() +
                        grade.getPresentation()
        );
        return gradeRepo.save(grade);
    }
    @PostMapping("/grades/{studentId}/submit")
    public Grade submitGrade(@PathVariable Long studentId) {

        Grade grade = gradeRepo
                .findByStudentProfileId(studentId);

        if (grade == null) {
            throw new RuntimeException("Grade not found");
        }

        grade.setSentToAdmin(true);

        return gradeRepo.save(grade);

    }

    //ANALYTICS
    @GetMapping("/analytics/{studentProfileId}")
    public Analytics getAnalytics(@PathVariable Long studentProfileId) {
        return analyticsRepo.findByStudentProfileId(studentProfileId);
    }

}

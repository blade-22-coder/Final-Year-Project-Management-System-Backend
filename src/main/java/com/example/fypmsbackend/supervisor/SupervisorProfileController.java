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
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    @Value("${file.upload-dir")
    private String uploadDir;
    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @RequestBody("file") MultipartFile file,
            @AuthenticationPrincipal User user
            ) throws IOException {

        SupervisorProfile supervisor = supervisorProfileRepo.findByUser(user)
                .orElseThrow(()-> new RuntimeException("Supervisor Not Found"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        supervisor.setProfileImagePath(fileName);
        supervisorProfileRepo.save(supervisor);

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

    //STUDENTS
    @GetMapping("/students")
    public List<StudentProfile> myStudents(
            @RequestParam String fullName,
            @RequestParam String registrationNumber){

        return supervisorStudentService.getMyStudents(fullName, registrationNumber);

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
    @GetMapping("/docs/{docId}/comment")
    public Documentation commentOnDoc(@PathVariable Long docId,
                                      @RequestBody String comment) {
        Documentation doc = documentationRepo.findById(docId).orElseThrow();
        doc.setSupervisorComment(comment);
        return documentationRepo.save(doc);
    }

    //GITHUB
    @GetMapping("/github/{studentProfileId}")
    public Github getGithub(@PathVariable Long studentProfileId) {
        return githubRepo.findById(studentProfileId).orElseThrow();
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
        return gradeRepo.findById(studentId).orElseThrow();
    }
    @PostMapping("/grades/{studentId}")
    public Grade saveGrade(@PathVariable Long studentId,
                           @RequestBody Grade grade) {
        grade.setTotal(
                grade.getProposal() +
                        grade.getProgress() +
                        grade.getFinalReport() +
                        grade.getPresentation()
        );
        return gradeRepo.save(grade);
    }

    //ANALYTICS
    @GetMapping("/analytics/{studentProfileId}")
    public Analytics getAnalytics(@PathVariable Long studentProfileId) {
        return analyticsRepo.findById(studentProfileId).orElseThrow();
    }

}

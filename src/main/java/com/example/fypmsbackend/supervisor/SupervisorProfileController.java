package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.deadline.Deadline;
import com.example.fypmsbackend.deadline.DeadlineRepository;
import com.example.fypmsbackend.grade.Grade;
import com.example.fypmsbackend.grade.GradeRepository;
import com.example.fypmsbackend.model.*;
import com.example.fypmsbackend.repository.*;
import com.example.fypmsbackend.security.AuthHelper;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
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
    private final AnalyticsRepository analyticsRepo;
    private final GradeRepository  gradeRepo;
    private final SupervisorStudentService supervisorStudentService;
    private final StudentProfileRepository studentRepo;
    private final DeadlineRepository deadlineRepo;


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
    @GetMapping("/my-students")
    public List<Map<String, Object>> getmyStudents(Authentication auth) {

        //Get supervisor
        String email =  auth.getName();
        SupervisorProfile supervisor =
                supervisorProfileRepo.findByUserEmail(email)
                        .orElseThrow(()-> new RuntimeException("Supervisor Not Found"));

        //Fetch all students supervised by this
        List<StudentProfile> students =
                studentRepo.findBySupervisor(supervisor);

        //Map @ student to JSON for progress
        return students.stream().map(student -> {

            //Get latest submission for particular student
            Submission sub = submissionRepo
                    .findLatestByStudentProfileId(student.getId()).orElse(null);

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
            map.put("fullName", student.getUser().getFullName());
            map.put("registrationNumber", student.getRegistrationNumber());
            map.put("profileImage", student.getProfileImagePath());
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
                              @RequestBody Map<String,String> payload) {
        User supervisor = authHelper.getCurrentUser();
        Submission sub = submissionRepo.findById(submissionId).orElseThrow();

        Comment c = new Comment();
        c.setMessage(payload.get("message"));
        c.setSupervisor(supervisor);
        c.setSubmission(sub);
        c.setAuthor("SUPERVSIOR");
        c.setCreatedAt(LocalDateTime.now());

        return commentRepo.save(c);
    }
    @GetMapping("/comments/{submissionId}/{type}")
    public List<Comment> getComments(@PathVariable Long submissionId,
                                     @PathVariable CommentType type) {
        return commentRepo.findBySubmissionId(submissionId);
    }


    //DOCUMENTATION
    @GetMapping("/file/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws IOException {
        try {

            Path path = Paths.get("uploads/proposals").resolve(fileName);

            System.out.println("Looking for file: " + path.toAbsolutePath());

            if (!Files.exists(path)) {
                path = Paths.get("uploads/finalReports").resolve(fileName);
            }

            if (!Files.exists(path)) {
                path = Paths.get("uploads/snapshots").resolve(fileName);
            }
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            //if it's DOCX -> convert to PDF
            if (fileName.endsWith(".docx")) {

                WordprocessingMLPackage wordMLPackage =
                        WordprocessingMLPackage.load(path.toFile());

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                Docx4J.toPDF(wordMLPackage, out);
                ByteArrayResource pdfResource = new ByteArrayResource(out.toByteArray());

                return ResponseEntity.ok()
                        .header("Content-Type", MediaType.APPLICATION_PDF.toString())
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=\"preview.pdf\"")
                        .body(pdfResource);

            }

            //otherwise return file normally
            String type = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(type != null ? type : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException | Docx4JException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//    @GetMapping("/docs/{studentProfileId}")
//    public List<Documentation> getDocs(@PathVariable Long studentProfileId) {
//        return documentationRepo.findByStudentProfileId(studentProfileId);
//    }
//    @PostMapping("/docs/{docId}/comment")
//    public Documentation commentOnDoc(@PathVariable Long docId,
//                                      @RequestBody String comment) {
//        Documentation doc = documentationRepo.findById(docId).orElseThrow();
//        doc.setSupervisorComment(comment);
//        return documentationRepo.save(doc);
//    }

//    //GITHUB
//    @GetMapping("/github/{studentProfileId}")
//    public ResponseEntity<?> getGithub(@PathVariable Long studentProfileId) {
//
//        Github github = githubRepo.findByStudentProfileId(studentProfileId);
//
//        if (github == null) {
//            return ResponseEntity.ok(Map.of(
//                    "repoUrl", "Not submitted yet",
//                    "lastCommit", "N/A",
//                    "id",0
//            ));
//        }
//        return ResponseEntity.ok(github);
//    }

    //SNAPSHOTS
    @GetMapping("/snapshots/{studentId}")
    public ResponseEntity<?> getSnapshots(@PathVariable Long studentId) {

        //Try fetching snapshots from latest submission
        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElse(null);

        if (sub == null || sub.getSnapshotsUrl() == null) {
            return ResponseEntity.ok(List.of());
        }

        //Fallback: fetch all snapshots
        List<String> snapshots =List.of(sub.getSnapshotsUrl().split(";"));
        return ResponseEntity.ok(snapshots);
    }

    @GetMapping("/snapshots/file/{fileName}")
    public ResponseEntity<Resource> serveSnapshot(
            @PathVariable String fileName) throws IOException {

        Path path = Paths.get("uploads/snapshots").resolve(fileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists())
            return ResponseEntity.notFound().build();

        String type = Files.probeContentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type))
                .body(resource);
    }

    //GRADES
    @GetMapping("/grades/{studentId}")
    public ResponseEntity<?> getGrade(@PathVariable Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().body("Student ID is required");
        }

        Optional<Grade> gradeOpt = gradeRepo.findByStudentProfile_Id(studentId);

        if (gradeOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "proposal",0,
                    "progress",0,
                    "finalReport",0,
                    "presentation",0
            ));
        }

        return ResponseEntity.ok(gradeOpt.get());
    }
    @PostMapping("/grades/{studentId}")
    public Grade saveGrade(@PathVariable Long studentId,
                           @RequestBody Grade grade) {
        Optional<Grade> existingOpt =
                gradeRepo.findByStudentProfile_Id(studentId);

        existingOpt.ifPresent(existing ->
                grade.setId(existing.getId()));

        StudentProfile student =
                studentRepo.findById(studentId).orElseThrow();

        grade.setStudentProfile(student);

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
                .findByStudentProfile_Id(studentId)
                .orElseThrow(()-> new RuntimeException("Grade not found"));

        grade.setSentToAdmin(true);

        return gradeRepo.save(grade);

    }

    //ANALYTICS
    @GetMapping("/analytics/{studentId}")
    public ResponseEntity<?> getAnalytics(@PathVariable Long studentId) {

        Analytics analytics = analyticsRepo.findByStudentProfileId(studentId);

        if (analytics == null) {
            Map<String,Object> empty = new HashMap<>();
            empty.put("progress", List.of(0,0,0,0));
            empty.put("repoCommits", List.of(0,0,0,0));
            return ResponseEntity.ok(empty);
        }
        return ResponseEntity.ok(analytics);
    }

    //DEADLINES
    @GetMapping("/deadlines")
    public List<Deadline> getDeadlines() {
        return deadlineRepo.findAll();
    }

    //APPROVAL
    //TITLE
    @PutMapping("/{studentId}/title/approve")
    public Submission approveTitle(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setTitleApproved(true);
        return submissionRepo.save(sub);
    }
    @PutMapping(("/{studentId}/title/reject"))
    public Submission rejectTitle(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setTitleApproved(false);
        sub.setTitleRejected(true);
        return submissionRepo.save(sub);
    }

    //PROPOSAL
    @PutMapping("/{studentId}/proposal/approve")
    public Submission approveProposal(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setProposalApproved(true);
        return submissionRepo.save(sub);
    }
    @PutMapping(("/{studentId}/proposal/reject"))
    public Submission rejectProposal(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setProposalApproved(false);
        sub.setProposalRejected(true);
        return submissionRepo.save(sub);
    }

    //FINAL REPORT
    @PutMapping("/{studentId}/finalReport/approve")
    public Submission approveFinalReport(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setFinalReportApproved(true);
        return submissionRepo.save(sub);
    }
    @PutMapping(("/{studentId}/finalReport/reject"))
    public Submission rejectFinalReport(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setFinalReportApproved(false);
        sub.setFinalReportRejected(true);
        return submissionRepo.save(sub);
    }

    //GITHUB
    @PutMapping("/{studentId}/githubLink/approve")
    public Submission approveGithubLink(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setGithubLinkApproved(true);
        return submissionRepo.save(sub);
    }
    @PutMapping(("/{studentId}/githubLink/reject"))
    public Submission rejectGithubLink(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setGithubLinkApproved(false);
        sub.setGithubLinkRejected(true);
        return submissionRepo.save(sub);
    }

    //SNAPSHOTS
    @PutMapping("/{studentId}/snapshots/approve")
    public Submission approveSnapshots(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setSnapshotsApproved(true);
        return submissionRepo.save(sub);
    }
    @PutMapping(("/{studentId}/snapshots/reject"))
    public Submission rejectSnapshots(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow();

        sub.setSnapshotsApproved(false);
        sub.setSnapshotsRejected(true);
        return submissionRepo.save(sub);
    }

    //SPECIFIC STUDENT SUBMISSIONS
    @GetMapping("/title/{studentId}")
    public ResponseEntity<?> getProjectTitle(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findTopByStudentProfileIdOrderBySubmittedAtDesc(studentId)
                .orElse(null);

        if(sub == null || sub.getProjectTitle() == null) {
            return ResponseEntity.ok(Map.of("title", null));
        }

        return ResponseEntity.ok(Map.of(
                "title", sub.getProjectTitle(),
                "approved", sub.isTitleApproved()
        ));
    }

    @GetMapping("/docs/{studentId}")
    public ResponseEntity<?> getDocumentation(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findTopByStudentProfileIdOrderBySubmittedAtDesc(studentId)
                .orElse(null);

        if(sub == null) {
            return ResponseEntity.ok(Map.of(
                    "proposal", null,
                    "finalReport", null
            ));
        }

        return ResponseEntity.ok(Map.of(
                "proposal", sub.getProposalUrl(),
                "proposalApproved", sub.isProposalApproved(),
                "finalReport", sub.getFinalReportUrl(),
                "reportApproved", sub.isFinalReportApproved()
        ));
    }

    @GetMapping("/github/{studentId}")
    public ResponseEntity<?> getGithubLink(@PathVariable Long studentId) {

        Submission sub = submissionRepo
                .findTopByStudentProfileIdOrderBySubmittedAtDesc(studentId)
                .orElse(null);

        if(sub == null) {
            return ResponseEntity.ok(Map.of("githubLink", null));
        }

        return ResponseEntity.ok(Map.of(
                "githubLink", sub.getGithubLink(),
                "approved", sub.isGithubLinkApproved()
        ));

    }


}


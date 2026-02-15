package com.example.fypmsbackend.uploads;

import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    private final SubmissionRepository submissionRepo;
    private final UserRepository userRepo;
    private final StudentProfileRepository studentRepo;

    public FileService(SubmissionRepository submissionRepo,
                       UserRepository userRepo,
                       StudentProfileRepository studentRepo) {
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
    }

    public ResponseEntity<Resource> streamSubmission(
            Long submissionId,
            Authentication auth
    ) throws IOException {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        Submission submission = submissionRepo.findById(submissionId).orElseThrow();

        //access control
        if(user.getRole() == Role.STUDENT) {
            StudentProfile student = studentRepo.findByUser(user).orElseThrow();
            if (!submission.getStudentProfile().getId().equals(student.getId())) {
                throw new AccessDeniedException("Forbidden");
            }
        }

        //supervisor & admin allowed
        Path path = Paths.get(submission.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if(!resource.exists()) {
            throw new FileNotFoundException("File not found");
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + submission.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}

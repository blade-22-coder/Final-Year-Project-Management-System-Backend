package com.example.fypmsbackend.service;

import com.example.fypmsbackend.model.Submission;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.SubmissionRepository;
import com.example.fypmsbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepo;
    private final UserRepository userRepo;

    private static final String UPLOAD_DIR = "uploads/";

    public SubmissionService(SubmissionRepository submissionRepo, UserRepository userRepo) {
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
    }

    public Submission upload(String type, MultipartFile file, Long studentId) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());

            User student = userRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Submission sub = new Submission();
            sub.setType(type);
            sub.setFilePath(UPLOAD_DIR + fileName);
            sub.setStatus("PENDING");
            sub.setStudent(student);

            return submissionRepo.save(sub);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }
    }

    public List<Submission> getByStudent(Long studentId) {
        return submissionRepo.findByStudentId(studentId);
    }
}

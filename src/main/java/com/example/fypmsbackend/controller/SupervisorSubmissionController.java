package com.example.fypmsbackend.controller;

import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.Submission;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/submission")
@RequiredArgsConstructor
public class SupervisorSubmissionController {

    private final SubmissionRepository submissionRepo;
    private final StudentProfileRepository studentProfileRepo;

    @GetMapping("/{studentId}")
    public List<Submission> getSubmissions(@PathVariable Long studentId) {

        StudentProfile student = studentProfileRepo.findById(studentId)
                .orElseThrow(()-> new RuntimeException("student not found with id: " + studentId));
        User studentUser = student.getUser();
        return submissionRepo.findByStudentId(studentId);
    }
}

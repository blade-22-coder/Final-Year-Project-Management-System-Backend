package com.example.fypmsbackend.controller;

import com.example.fypmsbackend.model.Submission;
import com.example.fypmsbackend.service.SubmissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService service;

    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Submission upload(
            @RequestParam String type,
            @RequestParam MultipartFile file,
            @RequestParam Long studentId
            ) {
        return service.upload(type, file, studentId);
    }

    @GetMapping("/student/{id}")
    public List<Submission> getStudentSubmissions(@PathVariable Long id) {
        return service.getByStudent(id);
    }
}

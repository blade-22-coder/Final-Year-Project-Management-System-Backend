package com.example.fypmsbackend.controller;

import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/students")
@RequiredArgsConstructor
public class SupervisorStudentController {

    private final StudentProfileRepository studentRepo;
    private final UserRepository userRepo;

    @GetMapping("/{supervisorId}")
    public List<StudentProfile> getAllocatedStudents(@PathVariable Long supervisorId) {

        User supervisor = userRepo.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("supervisor not found"));

        return studentRepo.findBySupervisor(supervisor);
    }
}

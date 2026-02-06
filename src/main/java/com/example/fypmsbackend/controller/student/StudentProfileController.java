package com.example.fypmsbackend.controller.student;

import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.SupervisorProfile;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private UserRepository userRepo;
    private StudentProfileRepository studentProfileRepo;

    @GetMapping
    public StudentProfile getProfile(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        return studentProfileRepo.findByUserId(user.getId()).orElseThrow();

    }
}

package com.example.fypmsbackend.controller.supervisor;

import com.example.fypmsbackend.model.SupervisorProfile;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.SupervisorProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supervisor/profile")
@RequiredArgsConstructor
public class SupervisorProfileController {

    private final UserRepository userRepo;
    private final SupervisorProfileRepository supervisorProfileRepo;

    @GetMapping
    public SupervisorProfile getProfile(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        return supervisorProfileRepo.findByUserId(user.getId()).orElseThrow();

    }
}

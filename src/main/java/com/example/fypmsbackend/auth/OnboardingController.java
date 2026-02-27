package com.example.fypmsbackend.auth;

import com.example.fypmsbackend.student.StudentOnboardingRequest;
import com.example.fypmsbackend.supervisor.SupervisorOnboardingRequest;
import com.example.fypmsbackend.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/onboarding")
@CrossOrigin
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    @PostMapping("/student")
    public ResponseEntity<?> student(
                                     @RequestBody StudentOnboardingRequest req,
                                     Principal principal) {

        User user = service.onboardStudent(principal.getName(), req);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/supervisor")
    public ResponseEntity<?> supervisor(
                                        @RequestBody SupervisorOnboardingRequest req,
                                        Principal principal) {

        User  user = service.onboardSupervisor(principal.getName(), req);
        return ResponseEntity.ok(user);
    }

}

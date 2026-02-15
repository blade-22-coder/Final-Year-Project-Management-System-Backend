package com.example.fypmsbackend.auth;

import com.example.fypmsbackend.student.StudentOnboardingRequest;
import com.example.fypmsbackend.supervisor.SupervisorOnboardingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@CrossOrigin
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    @PostMapping("/student/{userId}")
    public ResponseEntity<?> student(@PathVariable Long userId,
                                     @RequestBody StudentOnboardingRequest req) {
        service.onboardStudent(userId, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/supervisor/{userId}")
    public ResponseEntity<?> supervisor(@PathVariable Long userId,
                                        @RequestBody SupervisorOnboardingRequest req) {
        service.onboardSupervisor(userId, req);
        return ResponseEntity.ok().build();
    }

}

package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorProfileController {

    private final UserRepository userRepo;
    private final SupervisorProfileRepository supervisorProfileRepo;

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
}

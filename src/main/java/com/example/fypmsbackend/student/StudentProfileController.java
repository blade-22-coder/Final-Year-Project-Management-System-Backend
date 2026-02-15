package com.example.fypmsbackend.student;

import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private UserRepository userRepo;
    private StudentProfileRepository studentProfileRepo;

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardStudent(
            @RequestBody StudentOnboardingRequest req,
            Authentication auth) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow();

        if (user.isOnboarded())
            return ResponseEntity.badRequest().body("Already onboarded");

        StudentProfile profile  = new StudentProfile();
        profile.setUser(user);
        profile.setRegistrationNumber(req. registrationNumber());
        profile.setCourse(req.course());
        profile.setProjectTitle(req.projectTitle());

        studentProfileRepo.save(profile);

        user.setOnboarded(true);
        userRepo.save(user);

        return ResponseEntity.ok("Student Onboarded");
    }

    @GetMapping("/me")
    public StudentProfile myProfile(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        return studentProfileRepo.findByUserId(user.getId()).orElseThrow();

    }
}

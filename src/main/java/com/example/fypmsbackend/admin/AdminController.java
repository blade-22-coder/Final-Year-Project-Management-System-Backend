package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.dto.AdminStats;
import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final StudentProfileRepository studentRepo;
    private final SubmissionRepository submissionRepo;
    private final AdminService adminService;

    @GetMapping("/stats")
    public AdminStats dashboardStats() {
        return adminService.getStats();
    }

    @GetMapping("/users")
    public List<User> allUsers() {
        return userRepo.findAll();
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable long id) {
        User user = userRepo.findById(id).orElseThrow();
        user.setEnabled(false);
        userRepo.save(user);
        return ResponseEntity.ok("User has been disabled");
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable long id) {
        User user = userRepo.findById(id).orElseThrow();
        user.setEnabled(true);
        userRepo.save(user);
        return ResponseEntity.ok("User has been enabled");
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {

        Map<String, Object> data = new HashMap<>();

        data.put("students", userRepo.countByRole(Role.STUDENT));
        data.put("supervisors", userRepo.countByRole(Role.SUPERVISOR));
        data.put("submissions", submissionRepo.count());

        return data;
    }
}

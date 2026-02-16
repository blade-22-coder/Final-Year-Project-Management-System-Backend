package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.deadline.Deadline;
import com.example.fypmsbackend.deadline.DeadlineRepository;
import com.example.fypmsbackend.dto.AdminStats;
import com.example.fypmsbackend.dto.AllocationRequest;
import com.example.fypmsbackend.grade.Grade;
import com.example.fypmsbackend.grade.GradeRepository;
import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final AdminSupervisorService adminSupervisorService;
    private final SupervisorProfileRepository supervisorRepo;
    private final GradeRepository  gradeRepo;
    private final DeadlineRepository deadlineRepo;

    //DASHBOARD
    @GetMapping("/stats")
    public AdminStats dashboardStats() {
        return adminService.getStats();
    }
    @GetMapping("/overview")
    public Map<String, Object> overview() {

        Map<String, Object> data = new HashMap<>();

        data.put("students", userRepo.countByRole(Role.STUDENT));
        data.put("supervisors", userRepo.countByRole(Role.SUPERVISOR));
        data.put("submissions", submissionRepo.count());

        return data;
    }

    //USERS
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

    //SUPERVISORS
    @GetMapping("/supervisors")
    public List<SupervisorProfile> allSupervisors() {
        return supervisorRepo.findAll();
    }
    @GetMapping("/students")
    public List<StudentProfile> allStudents() {
        return studentRepo.findAll();
    }

    //ASSIGNMENTS
    @PostMapping("/assign")
    public StudentProfile assignSupervisor(
            @RequestBody AllocationRequest request) {

        return adminSupervisorService.assignSupervisor(request);
    }
    @PostMapping("/unassign/{studentId}")
    public void unassign(@PathVariable long studentId) {
        StudentProfile student =  studentRepo.findById(studentId).orElseThrow();
        student.setSupervisor(null);
        studentRepo.delete(student);
    }

    //PROJECTS
    @GetMapping("/projects")
    public List<Grade> allProjects() {
        return gradeRepo.findAll();
    }

    //ANALYTICS
    @GetMapping("/analytics/monthly")
    public List<Map<String, Object>> monthlyAnalytics() {

        return submissionRepo.monthlySubmissions()
                .stream()
                .map(row -> Map.of(
                        "month", row[0],
                        "count", row[1]
                ))
                .toList();
    }

    //DEADLINES
    @PostMapping("deadlines")
    public Deadline saveDeadline(@RequestBody Deadline d) {
        d.setCreatedAt(LocalDateTime.now());
        return deadlineRepo.save(d);
    }
    @GetMapping("/deadlines")
    public List<Deadline> allDeadlines() {
        return deadlineRepo.findAll();
    }


}

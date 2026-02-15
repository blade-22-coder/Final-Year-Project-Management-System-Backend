package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final StudentProfileRepository studentRepo;
    private final UserRepository userRepo;
    private final SupervisorProfileRepository supervisorRepo;

    @PostMapping("/assign")
    public void assign(
            @RequestParam Long studentId,
            @RequestParam Long supervisorId) {

        StudentProfile student = studentRepo.findById(studentId).orElseThrow();
        User user = userRepo.findById(supervisorId).orElseThrow();
        SupervisorProfile supervisor = supervisorRepo.findByUser(user).orElseThrow();

        student.setSupervisor(supervisor);
        studentRepo.save(student);
    }

    @PostMapping("/unassign/{studentId}")
        public void unassign(@PathVariable Long studentId) {
            StudentProfile student = studentRepo.findById(studentId).orElseThrow();
            student.setSupervisor(null);
            studentRepo.save(student);
        }
}

package com.example.fypmsbackend.controller.admin;

import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final StudentProfileRepository studentRepo;
    private final UserRepository userRepo;

    @PostMapping("/assign")
    public void assign(
            @RequestParam Long studentId,
            @RequestParam Long supervisorId) {

        StudentProfile student = studentRepo.findById(studentId).orElseThrow();
        User supervisor = userRepo.findById(supervisorId).orElseThrow();

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

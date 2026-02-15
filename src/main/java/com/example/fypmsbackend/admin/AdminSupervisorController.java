package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.dto.AllocationRequest;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/supervisors")
@RequiredArgsConstructor
public class AdminSupervisorController {

    private final AdminSupervisorService service;
    private final StudentProfileRepository studentRepo;
    private final SupervisorProfileRepository supervisorRepo;

    @PostMapping
    public ResponseEntity<String> assignSupervisor(
            @RequestBody AllocationRequest request) {

        service.assignSupervisor(
                request
        );

        return ResponseEntity.ok("Supervisor assigned successfully");
    }

    @GetMapping("/students")
    public List<StudentProfile> students() {
        return studentRepo.findAll();
    }

    @GetMapping("/list")
    public List<SupervisorProfile> supervisors() {
        return supervisorRepo.findAll();
    }
}

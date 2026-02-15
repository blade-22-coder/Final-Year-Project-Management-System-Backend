package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.dto.AllocationRequest;
import com.example.fypmsbackend.student.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/allocations")
@RequiredArgsConstructor
public class StudentAllocationController {

    private final AdminSupervisorService allocationService;

    @PostMapping
    public StudentProfile allocateStudent(@RequestBody AllocationRequest request) {
        return allocationService.assignSupervisor(request);
    }
}

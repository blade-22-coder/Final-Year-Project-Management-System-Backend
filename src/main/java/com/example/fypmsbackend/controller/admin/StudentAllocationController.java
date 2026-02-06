package com.example.fypmsbackend.controller.admin;

import com.example.fypmsbackend.dto.AllocationRequest;
import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.service.StudentAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/allocations")
@RequiredArgsConstructor
public class StudentAllocationController {

    private final StudentAllocationService allocationService;

    @PostMapping
    public StudentProfile allocateStudent(@RequestBody AllocationRequest request) {
        return allocationService.assignSupervisor(request);
    }
}

package com.example.fypmsbackend.controller.admin;

import com.example.fypmsbackend.model.SupervisorProfile;
import com.example.fypmsbackend.repository.SupervisorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/supervisors")
@RequiredArgsConstructor
public class AdminSupervisorController {

    private final SupervisorProfileRepository repo;

    @GetMapping
    public List<SupervisorProfile> all() {
        return repo.findAll();
    }
}

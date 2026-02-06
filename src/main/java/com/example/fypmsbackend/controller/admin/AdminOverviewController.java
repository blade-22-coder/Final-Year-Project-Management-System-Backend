package com.example.fypmsbackend.controller.admin;

import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.SubmissionRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/overview")
@RequiredArgsConstructor
public class AdminOverviewController {

    private final UserRepository userRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final SubmissionRepository submissionRepo;

    @GetMapping
    public Map<String, Object> overview() {

        Map<String, Object> data = new HashMap<>();

        data.put("students", userRepo.countByRole(Role.STUDENT));
        data.put("supervisors", userRepo.countByRole(Role.SUPERVISOR));
        data.put("submissions", submissionRepo.count());

        return data;
    }
}

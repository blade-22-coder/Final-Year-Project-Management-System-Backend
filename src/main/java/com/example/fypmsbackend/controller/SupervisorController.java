package com.example.fypmsbackend.controller;

import com.example.fypmsbackend.service.AnalyticsService;
import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorController {

    private final StudentProfileService studentProfileService;
    private final AnalyticsService analyticsService;


    //get students assigned to the currently logged-in supervisor
    @GetMapping("/students")
    public List<StudentProfile> getMyStudents(Authentication auth) {
        return studentProfileService.getStudentsForSupervisor(auth.getName());
    }

    //get analytics for a specific student
    @GetMapping("/analytics/{studentProfileId}")
    public Map<String, Object> analytics(@PathVariable Long studentProfileId) {
        return analyticsService.getAnalytics(studentProfileId);
    }
}

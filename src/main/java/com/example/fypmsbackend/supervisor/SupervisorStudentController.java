package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.student.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/students")
@RequiredArgsConstructor
public class SupervisorStudentController {

    private final SupervisorStudentService service;

    @GetMapping
    public List<StudentProfile> myStudents(
            @RequestParam String fullName,
            @RequestParam String registrationNumber){

        return service.getMyStudents(fullName, registrationNumber);

    }
}

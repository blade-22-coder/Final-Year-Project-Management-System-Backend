package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.grade.Grade;
import com.example.fypmsbackend.grade.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final GradeRepository gradeRepo;

    @GetMapping
    public List<Grade> all() {
        return gradeRepo.findAll();
    }

}

package com.example.fypmsbackend.controller.supervisor;

import com.example.fypmsbackend.model.Grade;
import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.repository.GradeRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor/grades")
public class SupervisorGradeController {

    private final GradeRepository repo;
    public SupervisorGradeController(GradeRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{StudentId}")
    public Grade getGrade(@PathVariable Long StudentId) {
        return repo.findByStudentProfileId(StudentId);
    }

    @PostMapping
    public Grade saveGrade(@PathVariable Long StudentId,
                           @RequestBody Grade grade) {
        grade.setStudentProfile(
                new StudentProfile(studentProfileId)
        );
        grade.setTotal(
                grade.getProposal()
                + grade.getProgress()
                + grade.getFinalReport()
                + grade.getPresentation()
        );
        return repo.save(grade);
    }
}

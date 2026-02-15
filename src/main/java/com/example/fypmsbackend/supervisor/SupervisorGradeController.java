package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.grade.Grade;
import com.example.fypmsbackend.grade.GradeRepository;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor/grades")
public class SupervisorGradeController {

    private final GradeRepository repo;
    private final StudentProfileRepository studentRepo;
    public SupervisorGradeController(GradeRepository repo,
                                     StudentProfileRepository studentRepo) {
        this.repo = repo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/{StudentId}")
    public Grade getGrade(@PathVariable Long StudentId) {
        return repo.findByStudentProfileId(StudentId);
    }

    @PostMapping
    public Grade saveGrade(@PathVariable Long StudentId,
                           @RequestBody Grade grade) {
        //grade.setStudentProfile(
        //        new StudentProfile(student)
      //  );
        grade.setTotal(
                grade.getProposal()
                + grade.getProgress()
                + grade.getFinalReport()
                + grade.getPresentation()
        );
        return repo.save(grade);
    }
}

package com.example.fypmsbackend.grade;

import com.example.fypmsbackend.model.GradeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@PreAuthorize("hasRole('SUPERVISOR')")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping("/{submissionId}")
    public ResponseEntity<?> grade(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest req,
            Authentication auth
    ) {
        gradeService.gradeSubmission(
                submissionId,
                req.score(),
                req.comment(),
                auth
        );

        return ResponseEntity.ok("Graded Successfully");
    }
}

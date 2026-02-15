package com.example.fypmsbackend.grade;

import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class GradeService {

    private final GradeRepository gradeRepo;
    private final SubmissionRepository submissionRepo;
    private final UserRepository userRepo;

    public GradeService(GradeRepository gradeRepo,
                        SubmissionRepository submissionRepo,
                        UserRepository userRepo) {
        this.gradeRepo = gradeRepo;
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
    }

    public Grade gradeSubmission(
            Long submissionId,
            Integer score,
            String comment,
            Authentication auth
    ) {
        User supervisor = userRepo.findByEmail(auth.getName()).orElseThrow();

        Submission submission = submissionRepo.findById(submissionId).orElseThrow();

        //enforcing supervisor ownership
        Grade grade = gradeRepo.findBySubmission(submission).orElse(new Grade());

        grade.setSubmission(String.valueOf(submission));
        grade.setscore(score);
        grade.setSupervisorComment(comment);


        return gradeRepo.save(grade);
    }

}

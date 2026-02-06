package com.example.fypmsbackend.controller.supervisor;

import com.example.fypmsbackend.model.Comment;
import com.example.fypmsbackend.model.Submission;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.CommentRepository;
import com.example.fypmsbackend.repository.SubmissionRepository;
import com.example.fypmsbackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/supervisor/comments")
public class SupervisorFeedbackController {

    private final CommentRepository commentRepo;
    private final SubmissionRepository submissionRepo;
    private final UserRepository userRepo;

    public SupervisorFeedbackController(CommentRepository commentRepo,
                                        SubmissionRepository submissionRepo,
                                        UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/{SubmissionId}")
    public Comment comment(
            @PathVariable Long submissionId,
            @PathVariable String message,
            @RequestParam Long supervisorId) {

        Submission sub = submissionRepo.findById(submissionId).orElseThrow();
        User supervisor = userRepo.findById(supervisorId).orElseThrow();

        Comment c =  new Comment();
        c.setMessage(message);
        c.setSubmission(sub);
        c.setSupervisor(supervisor);
        c.setcreatedAt(LocalDateTime.now());

        return commentRepo.save(c);
    }

    @GetMapping("/{submissionId}")
    public List<Comment> getComments(@PathVariable Long submissionId) {
        return commentRepo.findSubmissionId(submissionId);
    }
}

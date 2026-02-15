package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.submission.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supervisor/approve")
@RequiredArgsConstructor
public class SupervisorApprovalController {

    private final SubmissionRepository submissionRepo;

    @PostMapping("/{studentId}/{type}")
    public Submission approve(
            @PathVariable Long submissionId,
            @PathVariable String type) {

        Submission sub = submissionRepo.findById(submissionId).orElseThrow();

        switch(type) {
            case "title" -> sub.setTitleApproved(true);
            case "proposal" -> sub.setProposalApproved(true);
            case "finalReport" -> sub.setFinalReportApproved(true);
            case "githubLink" -> sub.setGithubLinkApproved(true);
            case "snapshots" -> sub.setSnapshotsApproved(true);
            default -> throw new RuntimeException("Invalid approval type");
        }

        return submissionRepo.save(sub);

    }

}

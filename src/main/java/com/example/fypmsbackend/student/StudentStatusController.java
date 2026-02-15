package com.example.fypmsbackend.student;

import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/student/status")
@RequiredArgsConstructor
public class StudentStatusController {

    private final SubmissionRepository submissionRepo;
    private final BatteryService batteryService;


    @GetMapping("/{studentId}")
    public Map<String, Object> getStatus(@PathVariable Long studentId) {

        var sub = submissionRepo
                .findLatestByStudentProfileId(studentId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        int battery = batteryService.calculateBattery(sub);

        return Map.of(
                "battery", battery,
                "titleApproved", sub.isTitleApproved(),
                "proposalApproved", sub.isProposalApproved(),
                "finalReportApproved", sub.isFinalReportApproved(),
                "githubLinkApproved", sub.isGithubLinkApproved(),
                "snapshotsApproved", sub.isSnapshotsApproved()
        );
    }
}

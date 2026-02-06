package com.example.fypmsbackend.controller.student;

import com.example.fypmsbackend.repository.SubmissionRepository;
import com.example.fypmsbackend.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/{studentId}")
@RequiredArgsConstructor
public class StudentStatusController {

    private final SubmissionRepository submissionRepo;
    private final BatteryService batteryService;

    @GetMapping("/{studentId}")
    public Map<String, Object> getStatus(@PathVariable String studentId) {

        int battery = batteryService.calculateBattery(sub);

        return Map.of(
                "battery", battery,
                "titleApproved", sub.isTitleApproved(),
                "proposalApproved", sub.isProposalApproved(),
                "finalReportApproved", sub.isFinalReportApproved(),
                "githubLinkApproved", sub.isGithubLinkapproved(),
                "snapshotsApproved", sub.isSnapshotsApproved()
        );
    }
}

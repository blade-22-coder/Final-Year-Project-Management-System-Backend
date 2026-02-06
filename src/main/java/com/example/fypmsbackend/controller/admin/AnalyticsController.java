package com.example.fypmsbackend.controller.admin;

import com.example.fypmsbackend.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final SubmissionRepository submissionRepo;

    @GetMapping("/monthly")
    public List<Map<String, Object>> monthly() {

        return submissionRepo.monthlySubmissions()
                .stream()
                .map(row -> Map.of(
                        "month", row[0],
                        "count", row[1]
                ))
                .toList();
    }
}

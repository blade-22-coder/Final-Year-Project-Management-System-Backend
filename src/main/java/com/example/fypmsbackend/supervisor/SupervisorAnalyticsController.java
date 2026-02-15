package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.model.Analytics;
import com.example.fypmsbackend.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supervisor/analytics")
@RequiredArgsConstructor
public class SupervisorAnalyticsController {

    private final AnalyticsRepository repo;

    @GetMapping("/{studentProfileId}")
    public Analytics getAnalytics(@PathVariable Long studentProfileId) {
        return repo.findById(studentProfileId).orElseThrow();
    }
}

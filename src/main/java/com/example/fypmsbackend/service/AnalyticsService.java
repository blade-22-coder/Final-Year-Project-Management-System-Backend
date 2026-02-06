package com.example.fypmsbackend.service;

import com.example.fypmsbackend.repository.SubmissionRepository;
import com.example.fypmsbackend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SubmissionRepository submissionRepo;
    private final CommentRepository commentRepo;

    public Map<String, Object> getAnalytics(Long studentProfileId) {

        Map<String, Object> data = new HashMap<>();

        data.put("submissions",
                submissionRepo.countByStudentProfileId(studentProfileId));

        data.put("comments",
                commentRepo.countBySubmissionStudentProfileId(studentProfileId));

        data.put("lastSubmission",
                submissionRepo.findLatestSubmission(studentProfileId));

        return data;
    }
}

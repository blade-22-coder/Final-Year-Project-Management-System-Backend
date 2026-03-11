package com.example.fypmsbackend.dto;

import com.example.fypmsbackend.submission.Submission;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Submission submission,
        String comment,
        LocalDateTime createdAt
) {
}

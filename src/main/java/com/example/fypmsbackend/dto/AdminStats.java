package com.example.fypmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStats {
    private long students;
    private long supervisors;
    private long proposals;
    private long reports;

    private Map<String, Long> submissionsByStatus;
    private Map<String, Long> userByRole;
    private List<ActivityPoint> activity;
}
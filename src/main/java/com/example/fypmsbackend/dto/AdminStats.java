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
    private long totalStudents;
    private long totalSupervisors;
    private long totalProposals;
    private long totalReports;

    private long approvedCount;
    private long rejectedCount;
    private long pendingCount;

    private long totalAdmins;

    private List<ActivityPoint> monthlyActivity;
}
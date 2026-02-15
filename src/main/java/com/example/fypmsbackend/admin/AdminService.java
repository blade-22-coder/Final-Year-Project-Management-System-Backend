package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.dto.ActivityPoint;
import com.example.fypmsbackend.dto.AdminStats;
import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.model.Status;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final UserRepository userRepo;
    private final StudentProfileRepository studentRepo;
    private final SupervisorProfileRepository supervisorRepo;
    private final SubmissionRepository submissionRepo;

    public AdminService(UserRepository userRepo,
                        StudentProfileRepository studentRepo,
                        SupervisorProfileRepository supervisorRepo,
                        SubmissionRepository submissionRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.submissionRepo = submissionRepo;
        this.supervisorRepo = supervisorRepo;
    }

    public AdminStats getStats() {

        long students = studentRepo.count();
        long supervisors = supervisorRepo.count();
        long proposals = submissionRepo.countByProposalSubmittedTrue();
        long reports = submissionRepo.countByFinalReportSubmittedTrue();

        Map<String, Long> submissionsByStatus = Map.of(
                "APPROVED", submissionRepo.countByStatus(Status.APPROVED),
                "PENDING", submissionRepo.countByStatus(Status.PENDING),
                "REJECTED", submissionRepo.countByStatus(Status.REJECTED)
        );

        Map<String, Long> userByRole = Map.of(
                "STUDENT", userRepo.countByRole(Role.STUDENT),
                "SUPERVISOR", userRepo.countByRole(Role.SUPERVISOR)
        );

        List<ActivityPoint> activity =
                submissionRepo.monthlySubmissions()
                        .stream()
                        .map(row -> new ActivityPoint(
                                row[0].toString(),
                                (Long) row[1]
                        ))
                        .toList();

        return new AdminStats(
                students,
                supervisors,
                proposals,
                reports,
                submissionsByStatus,
                userByRole,
                activity
        );
    }

}

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

        long students = userRepo.countByRole(Role.STUDENT);
        long supervisors = userRepo.countByRole(Role.SUPERVISOR);
        long admins = userRepo.countByRole(Role.ADMIN);

        long proposals = submissionRepo.countByProposalSubmittedTrue();
        long reports = submissionRepo.countByFinalReportSubmittedTrue();

        long approved = submissionRepo.countByStatus(Status.APPROVED);
        long rejected = submissionRepo.countByStatus(Status.REJECTED);
        long pending = submissionRepo.countByStatus(Status.PENDING);


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
                approved,
                rejected,
                pending,
                admins,
                activity
        );
    }

}

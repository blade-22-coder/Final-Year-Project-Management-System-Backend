package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.dto.ActivityPoint;
import com.example.fypmsbackend.dto.AdminStats;
import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.model.Status;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.submission.SubmissionRepository;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import com.example.fypmsbackend.user.UserRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void deleteStudentById(Long studentId) {

        //delete submission first
        submissionRepo.deleteByStudentProfileId(studentId);

        //delete student + user
        var student = studentRepo.findById(studentId).orElseThrow();
        studentRepo.delete(student);
        userRepo.delete(student.getUser());

    }

    public AdminStats getStats() {

        long students = userRepo.countByRole(Role.STUDENT);
        long supervisors = userRepo.countByRole(Role.SUPERVISOR);
        long admins = userRepo.countByRole(Role.ADMIN);

        long proposals = submissionRepo.countByProposalSubmittedTrue();
        long reports = submissionRepo.countByFinalReportSubmittedTrue();

        long approvedProposals = submissionRepo.countByProposalApprovedTrue();
        long rejectedProposals = submissionRepo.countByProposalRejectedTrue();

        long approvedReports = submissionRepo.countByFinalReportApprovedTrue();
        long rejectedReports = submissionRepo.countByFinalReportRejectedTrue();


        List<ActivityPoint> activity =
                submissionRepo.dailySubmissions()
                        .stream()
                        .map(row -> new ActivityPoint(
                                row[0].toString(),
                                ((Number) row[1]).longValue()
                        ))
                        .toList();

        return new AdminStats(
                students,
                supervisors,
                proposals,
                reports,
                approvedProposals,
                rejectedProposals,
                approvedReports,
                rejectedReports,
                admins,
                activity
        );
    }

}

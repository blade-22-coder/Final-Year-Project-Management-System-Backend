package com.example.fypmsbackend.auth;

import com.example.fypmsbackend.student.StudentOnboardingRequest;
import com.example.fypmsbackend.supervisor.SupervisorOnboardingRequest;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import com.example.fypmsbackend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OnboardingService {

    private final UserRepository userRepo;
    private final StudentProfileRepository studentRepo;
    private final SupervisorProfileRepository supervisorRepo;

    public OnboardingService(UserRepository userRepo,
                             StudentProfileRepository studentRepo,
                             SupervisorProfileRepository supervisorRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
    }

    @Transactional
    public User onboardStudent(String email, StudentOnboardingRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isOnboarded()) {
            throw new RuntimeException("User is already onboarded");
        }

        StudentProfile profile = new StudentProfile();
        profile.setRegistrationNumber(req.registrationNumber());
        profile.setCourse(req.course());
        profile.setProjectTitle(req.projectTitle());
        profile.setUser(user);

        studentRepo.save(profile);

        user.setOnboarded(true);
        return userRepo.save(user);
    }

    @Transactional
    public User onboardSupervisor(String email, SupervisorOnboardingRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(()  -> new RuntimeException("User not found"));

        SupervisorProfile profile = new SupervisorProfile();
        profile.setStaffId(req.staffId());
        profile.setDepartment(req.department());
        profile.setMaxStudent(req.maxStudents());
        profile.setUser(user);

        supervisorRepo.save(profile);

        user.setOnboarded(true);
        return userRepo.save(user);

    }

}

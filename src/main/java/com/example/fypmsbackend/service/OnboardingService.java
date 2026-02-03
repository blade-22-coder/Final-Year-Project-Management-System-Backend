package com.example.fypmsbackend.service;

import com.example.fypmsbackend.dto.StudentOnboardingRequest;
import com.example.fypmsbackend.dto.SupervisorOnboardingRequest;
import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.SupervisorProfile;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.SupervisorProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
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

    public void onboardStudent(Long userId, StudentOnboardingRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isOnboarded()) {
            throw new RuntimeException("User is already onboarded");
        }

        StudentProfile profile = new StudentProfile();
        profile.setRegistrationNumber(req.registrationNumber);
        profile.setCourse(req.course);
        profile.setProjectTitle(req.projectTitle);
        profile.setUser(user);

        studentRepo.save(profile);

        user.setOnboarded(true);
        userRepo.save(user);
    }

    public void onboardSupervisor(Long userId, SupervisorOnboardingRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(()  -> new RuntimeException("User not found"));

        SupervisorProfile profile = new SupervisorProfile();
        profile.setStaffId(req.staffId);
        profile.setDepartment(req.department);
        profile.setMaxStudent(req.maxStudents);
        profile.setUser(user);

        supervisorRepo.save(profile);

        user.setOnboarded(true);
        userRepo.save(user);

    }

}

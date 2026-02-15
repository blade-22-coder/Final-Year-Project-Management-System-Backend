package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.student.StudentProfileRepository;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupervisorStudentService {

    private final StudentProfileRepository studentRepo;
    private final SupervisorProfileRepository supervisorRepo;
    private final UserRepository userRepo;

    public List<StudentProfile> getMyStudents(String fullName,
                                              String registrationNumber) {

        User user = userRepo.findByFullName(fullName)
                .orElseThrow(()-> new RuntimeException("User not found"));

        SupervisorProfile supervisor = supervisorRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        return studentRepo.findBySupervisor(supervisor);

    }
}

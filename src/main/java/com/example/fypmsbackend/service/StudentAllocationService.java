package com.example.fypmsbackend.service;

import com.example.fypmsbackend.dto.AllocationRequest;
import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentAllocationService {

    private final StudentProfileRepository studentRepo;
    private final UserRepository userRepo;

    public StudentProfile assignSupervisor(AllocationRequest request) {

        StudentProfile student =
                studentRepo.findById(request.getStudentProfileId())
                        .orElseThrow(() -> new RuntimeException("Student not found"));

        User supervisor =
                userRepo.findById(request.getSupervisorId())
                        .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        student.setSupervisor(supervisor);
        return studentRepo.save(student);
    }
}

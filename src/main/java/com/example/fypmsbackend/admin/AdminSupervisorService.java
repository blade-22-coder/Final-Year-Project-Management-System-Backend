package com.example.fypmsbackend.admin;

import com.example.fypmsbackend.dto.AllocationRequest;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfileRepository;
import com.example.fypmsbackend.student.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminSupervisorService {

    private final StudentProfileRepository studentRepo;
    private final SupervisorProfileRepository supervisorRepo;

    public StudentProfile assignSupervisor(AllocationRequest request) {

        StudentProfile student =
                studentRepo.findById(request.getStudentProfileId())
                        .orElseThrow(() -> new RuntimeException("Student not found"));

        SupervisorProfile supervisor =
                supervisorRepo.findById(request.getSupervisorProfileId())
                        .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        student.setSupervisor(supervisor);
         return studentRepo.save(student);
    }
}

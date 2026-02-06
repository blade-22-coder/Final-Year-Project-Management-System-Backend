package com.example.fypmsbackend.service;

import com.example.fypmsbackend.model.StudentProfile;
import com.example.fypmsbackend.repository.StudentProfileRepository;
import com.example.fypmsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepo;
    private final UserRepository userRepo;

    public List<StudentProfile> getStudentsForSupervisor(String supervisorEmail) {

        return studentProfileRepo.findBySupervisorUserEmail(supervisorEmail);
    }
}

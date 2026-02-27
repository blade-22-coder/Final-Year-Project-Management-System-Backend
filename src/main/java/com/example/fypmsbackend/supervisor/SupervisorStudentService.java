package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.security.AuthHelper;
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
    private final AuthHelper authHelper;

    public List<StudentProfile> getMyStudents() {
        User supervisor = authHelper.getCurrentUser();

        return studentRepo.findBySupervisor_Id(supervisor.getId());

    }
}


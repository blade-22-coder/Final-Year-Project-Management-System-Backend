package com.example.fypmsbackend.student;

import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByUser(User user);
    List<StudentProfile> findBySupervisorUserEmail(String supervisorEmail);

    List<StudentProfile>findBySupervisor_Id(Long Id);

}

package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class SupervisorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String staffId;
    private String department;
    private Integer maxStudent;
    private String profileImagePath;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "supervisor")
    private List<StudentProfile> studentProfiles;


    //getters and setters



}

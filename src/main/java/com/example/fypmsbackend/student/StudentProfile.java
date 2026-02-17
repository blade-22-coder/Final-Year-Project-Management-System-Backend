package com.example.fypmsbackend.student;

import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String course;
    private String projectTitle;
    private String profileImagePath;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private SupervisorProfile supervisor; //assigned by admin


    //getters & setters

}

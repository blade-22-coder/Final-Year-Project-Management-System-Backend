package com.example.fypmsbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "analytics")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int progress;
    private int commits;
    private String submissionStatus;

    @OneToOne
    @JoinColumn(name = "student_profile_id")
    private StudentProfile studentProfile;
}

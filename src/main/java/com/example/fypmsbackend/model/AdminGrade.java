package com.example.fypmsbackend.model;

import com.example.fypmsbackend.student.StudentProfile;
import jakarta.persistence.*;

@Entity
public class AdminGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private StudentProfile studentProfile;

    private int total;

    private boolean approved; //admin approves
}

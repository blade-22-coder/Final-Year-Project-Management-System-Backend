package com.example.fypmsbackend.model;

import com.example.fypmsbackend.student.StudentProfile;
import jakarta.persistence.*;

@Entity
@Table(name = "github")
public class Github {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubUrl;

    @Column(length = 10000)
    private String supervisorComment;

    @OneToOne
    @JoinColumn(name = "student_profile_id")
    private StudentProfile studentProfile;

    //getter and setters
    public String getSupervisorComment() {
        return supervisorComment;
    }
    public void setSupervisorComment(String supervisorComment) {
        this.supervisorComment = supervisorComment;
    }
}

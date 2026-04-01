package com.example.fypmsbackend.grade;

import com.example.fypmsbackend.model.Status;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grades")
@Setter
@Getter
public class Grade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;
    
    @OneToOne
    @JoinColumn(name = "supervisor_profile_id")
    private SupervisorProfile supervisor;

    private int proposal;
    private int progress;
    private int finalReport;
    private int presentation;
    private int total;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 10000)
    private String supervisorComment;

    private boolean sentToAdmin; //supervisor submits
    private boolean approved; //admin approves

    @OneToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    private Integer score;

    private String fullName;

    private String registrationNumber;

    private String projectTitle;

    //getters and setters

    public void setscore(Integer score) {
        this.score = score;
    }
   
}

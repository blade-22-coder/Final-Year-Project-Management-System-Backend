package com.example.fypmsbackend.grade;

import com.example.fypmsbackend.model.Status;
import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.supervisor.SupervisorProfile;
import com.example.fypmsbackend.user.User;
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

    @OneToOne
    @JoinColumn(name = "student_profile_id")
    private StudentProfile studentprofile;
    
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
    
    private String submission;
    private Integer score;

    public void setscore(Integer score) {
    }

    public void setStudentProfile(StudentProfile studentProfile) {

    }

    public void setSupervisorProfile(User supervisor) {

    }

    //getters and setters
   
}

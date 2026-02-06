package com.example.fypmsbackend.model;

import jakarta.persistence.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Entity
public class Grade {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_profile_id")
    private StudentProfile studentprofile;

    private int proposal;
    private int progress;
    private int finalReport;
    private int presentation;
    private int total;

    @Column(length = 10000)
    private String supervisorComment;

    private boolean sentToAdmin; //supervisor submits
    private boolean approved; //admin approves

    //getters and setters
    public int getProposal() {
        return proposal;
    }
    public void setProposal(int proposal) {
        this.proposal = proposal;
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public int getFinalReport() {
        return finalReport;
    }
    public void setFinalReport(int finalReport) {
        this.finalReport = finalReport;
    }
    public int getPresentation() {
        return presentation;
    }
    public void setPresentation(int presentation) {
        this.presentation = presentation;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public String getSupervisorComment() {
        return supervisorComment;
    }
    public void setSupervisorComment(String supervisorComment) {
        this.supervisorComment = supervisorComment;
    }
    public StudentProfile getStudentprofile() {
        return studentprofile;
    }
    public void setStudentprofile(StudentProfile studentprofile) {
        this.studentprofile = studentprofile;
    }
}

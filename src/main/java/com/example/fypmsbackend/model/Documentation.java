package com.example.fypmsbackend.model;

import com.example.fypmsbackend.student.StudentProfile;
import jakarta.persistence.*;

@Entity
@Table(name = "documentation")
public class Documentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; //proposal, final report

    private String fileUrl;

    @Column(length = 10000)
    private String supervisorComment;

    @ManyToOne
    @JoinColumn(name = "student_profile_id")
    private StudentProfile studentProfile;

    //getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFileUrl() {
        return fileUrl;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public String getSupervisorComment() {
        return supervisorComment;
    }
    public void setSupervisorComment(String supervisorComment) {
        this.supervisorComment = supervisorComment;
    }
    public StudentProfile getStudentProfile() {
        return studentProfile;
    }
    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }
}

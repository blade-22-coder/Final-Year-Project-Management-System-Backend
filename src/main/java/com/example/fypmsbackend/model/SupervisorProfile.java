package com.example.fypmsbackend.model;

import jakarta.persistence.*;

@Entity
public class SupervisorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String staffId;
    private String department;
    private Integer maxStudent;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    //getters and setters

    public String getStaffId() {
        return staffId;
    }
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public Integer getMaxStudent() {
        return maxStudent;
    }
    public void setMaxStudent(Integer maxStudent) {
        this.maxStudent = maxStudent;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }


}

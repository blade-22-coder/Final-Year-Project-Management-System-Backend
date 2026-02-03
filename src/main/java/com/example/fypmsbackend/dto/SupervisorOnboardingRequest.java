package com.example.fypmsbackend.dto;

public class SupervisorOnboardingRequest {
    public String staffId;
    public String department;
    public Integer maxStudents;

    //getters & setters
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
    public Integer getMaxStudents() {
        return maxStudents;
    }
    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }


}

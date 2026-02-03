package com.example.fypmsbackend.dto;

import lombok.Data;

@Data
public class OnboardingRequest {
    private String registrationNumber; //for students
    private String staffId; //for supervisors
    private String course;
    private String department;
    private Integer maxStudents;

}

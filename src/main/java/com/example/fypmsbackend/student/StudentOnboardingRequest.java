package com.example.fypmsbackend.student;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public record StudentOnboardingRequest (
     String registrationNumber,
     String course,
     String projectTitle) {}

    //getters & setters


package com.example.fypmsbackend.deadline;

import com.example.fypmsbackend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime deadlineDate;
    private LocalDateTime createdAt;

    //getters & setters



    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }
    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;}
}

package com.example.fypmsbackend.model;

import com.example.fypmsbackend.submission.Submission;
import com.example.fypmsbackend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Setter
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String message;

    private String author;  //student or supervisor
    private LocalDateTime CreatedAt;

    @ManyToOne
    private User student;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    public void setCreatedAt(LocalDateTime now) {
    }

    //getters & setters
}

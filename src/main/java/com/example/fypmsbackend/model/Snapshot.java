package com.example.fypmsbackend.model;

import com.example.fypmsbackend.student.StudentProfile;
import com.example.fypmsbackend.submission.Submission;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "snapshots")
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "submissio_id")
    private Submission submission;

    @ManyToOne
    private StudentProfile studentProfile;

    private LocalDateTime uploadedAt;
}

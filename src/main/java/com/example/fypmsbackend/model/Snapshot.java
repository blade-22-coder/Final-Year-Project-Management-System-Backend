package com.example.fypmsbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "snapshots")
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "submissio_id")
    private Submission submission;
}

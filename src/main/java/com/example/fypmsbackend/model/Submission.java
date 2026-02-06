package com.example.fypmsbackend.model;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectTitle;
    private String proposalUrl;
    private String finalReportUrl;
    private String githubLink;
    private String snapshot;

    //upload flags
    private boolean titleSubmitted;
    private boolean proposalSubmitted;
    private boolean finalReportSubmitted;
    private boolean githubSubmitted;
    private boolean snapshotSubmitted;

    //approval flags
    private boolean titleApproved;
    private boolean proposalApproved;
    private boolean finalReportApproved;
    private boolean githubApproved;
    private boolean snapshotApproved;




    private String filePath;
    private String status; //waiting, pending, approved, rejected
    private String comment;
    private LocalDateTime submittedAt;
    

    @ManyToOne
    private User student;

    //getters & setters
}


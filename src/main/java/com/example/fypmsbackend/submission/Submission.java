package com.example.fypmsbackend.submission;

import com.example.fypmsbackend.model.Comment;
import com.example.fypmsbackend.model.Status;
import com.example.fypmsbackend.student.StudentProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectTitle;
    private String proposalUrl;
    private String finalReportUrl;
    private String githubLink;
    private String snapshotsUrl;

    //upload flags
    private boolean titleSubmitted;
    private boolean proposalSubmitted;
    private boolean finalReportSubmitted;
    private boolean githubLinkSubmitted;
    private boolean snapshotsSubmitted;

    //approval flags
    private boolean titleApproved;
    private boolean proposalApproved;
    private boolean finalReportApproved;
    private boolean githubLinkApproved;
    private boolean snapshotsApproved;

    //rejected flags
    private boolean titleRejected;
    private boolean proposalRejected;
    private boolean finalReportRejected;
    private boolean githubLinkRejected;
    private boolean snapshotsRejected;

    private String filePath;

    @Enumerated(EnumType.STRING)
    private Status status; //waiting, pending, approved, rejected

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "submission")
    private List<Comment> comments;
    private LocalDateTime submittedAt;
    private String FileName;
    

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile studentProfile;


    //getters & setters

}


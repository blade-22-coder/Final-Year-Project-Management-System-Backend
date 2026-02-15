package com.example.fypmsbackend.model;

import com.example.fypmsbackend.user.User;
import jakarta.persistence.*;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String type;

    @ManyToOne
    private User user;

    //getters & setters
}

package com.example.fypmsbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.security.PrivilegedAction;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String role;
    private Long userId;

    public AuthResponse(String token, String role,  Long userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }
}

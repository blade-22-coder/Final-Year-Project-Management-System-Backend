package com.example.fypmsbackend.security;

import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {

    private final UserRepository userRepo;

    public AuthHelper(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

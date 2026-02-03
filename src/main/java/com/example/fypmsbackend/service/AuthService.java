package com.example.fypmsbackend.service;

import com.example.fypmsbackend.dto.AuthResponse;
import com.example.fypmsbackend.dto.LoginRequest;
import com.example.fypmsbackend.dto.SignupRequest;
import com.example.fypmsbackend.model.User;
import com.example.fypmsbackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepo = userRepository;
        this.userRepository = userRepository;
    }

    public  void register(SignupRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setOnboarded(false);

        userRepository.save(user);
    }


    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect credentials");
        }

        return new AuthResponse("DUMMY_TOKEN", user.getRole().name());
    }
}

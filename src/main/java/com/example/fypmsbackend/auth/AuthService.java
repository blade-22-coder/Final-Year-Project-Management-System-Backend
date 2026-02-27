package com.example.fypmsbackend.auth;

import com.example.fypmsbackend.dto.AuthResponse;
import com.example.fypmsbackend.dto.LoginRequest;
import com.example.fypmsbackend.dto.SignupRequest;
import com.example.fypmsbackend.security.JwtUtil;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo,
                       JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(SignupRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already exists");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setEnabled(true);
        user.setOnboarded(false);

        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getEmail(),  user.getRole());

        return  new AuthResponse(token, user.getRole().name(), user.getId());

    }


    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getRole().name(),  user.getId());
    }
}
